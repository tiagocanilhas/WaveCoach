package waveCoach.http.pipeline

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import waveCoach.domain.AuthenticatedUser
import waveCoach.domain.UserDomainConfig
import waveCoach.http.Uris
import waveCoach.http.pipeline.AuthenticatedUserArgumentResolver.Companion.addUserTo
import waveCoach.http.pipeline.AuthenticatedUserArgumentResolver.Companion.getUserFrom

@Component
class AuthenticationInterceptor(
    private val tokenProcessor: RequestTokenProcessor,
    private val usersDomainConfig: UserDomainConfig,
) : HandlerInterceptor {
    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
    ): Boolean {
        if (
            handler is HandlerMethod &&
            handler.methodParameters.any { it.parameterType == AuthenticatedUser::class.java }
        ) {
            val userAuthHeader =
                tokenProcessor
                    .processAuthorizationHeaderValue(request.getHeader(AUTHORIZATION_HEADER))

            val userCookie =
                tokenProcessor
                    .processAuthorizationCookieValue(request.getHeader(COOKIE_HEADER))

            val authenticatedUser = userAuthHeader ?: userCookie

            return when {
                userAuthHeader == null && userCookie == null -> {
                    response.status = 401
                    response.addHeader(WWW_AUTHENTICATE_HEADER, RequestTokenProcessor.SCHEME)
                    false
                }
                else -> {
                    addUserTo(authenticatedUser!!, request)
                    true
                }
            }
        }

        return true
    }

    override fun postHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        modelAndView: ModelAndView?,
    ) {
        if (request.requestURI == Uris.Users.LOGOUT) {
            return
        }

        val authenticatedUser = getUserFrom(request) ?: return

        val refreshedTime = usersDomainConfig.tokenRollingTtl.inWholeSeconds
        val refreshedTokenCookie =
            ResponseCookie.from("token", authenticatedUser.token)
                .httpOnly(true).path("/").maxAge(refreshedTime).build()

        val refreshedUserCookie =
            ResponseCookie.from(
                "user",
                "${authenticatedUser.info.id}:${authenticatedUser.info.username}",
            )
                .path("/").maxAge(refreshedTime).build()

        response.addHeader("Set-Cookie", refreshedTokenCookie.toString())
        response.addHeader("Set-Cookie", refreshedUserCookie.toString())
    }

    companion object {
        const val AUTHORIZATION_HEADER = "Authorization"
        const val COOKIE_HEADER = "Cookie"

        private const val WWW_AUTHENTICATE_HEADER = "WWW-Authenticate"
    }
}

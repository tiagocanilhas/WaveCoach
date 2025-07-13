package waveCoach.http.pipeline

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import waveCoach.domain.AuthenticatedCoach
import waveCoach.domain.AuthenticatedUser
import waveCoach.http.pipeline.AuthenticatedUserArgumentResolver.Companion.addUserTo

@Component
class AuthenticationInterceptor(
    private val tokenProcessor: RequestTokenProcessor,
) : HandlerInterceptor {
    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
    ): Boolean {
        if (
            handler is HandlerMethod &&
            handler.methodParameters.any {
                it.parameterType == AuthenticatedUser::class.java ||
                    it.parameterType == AuthenticatedCoach::class.java
            }
        ) {
            val authenticatedUser = processAuthorization(request)

            return when {
                authenticatedUser == null -> {
                    response.status = 401
                    response.addHeader(WWW_AUTHENTICATE_HEADER, RequestTokenProcessor.SCHEME)
                    false
                }
                else -> {
                    addUserTo(authenticatedUser, request)
                    true
                }
            }
        }

        return true
    }

    private fun processAuthorization(request: HttpServletRequest): AuthenticatedUser? {
        val headerValue = request.getHeader(AUTHORIZATION_HEADER)
        val cookieValue = request.getHeader(COOKIE_HEADER)

        val header = headerValue?.let { tokenProcessor.processAuthorizationHeaderValue(it) }
        val cookie = cookieValue?.let { tokenProcessor.processAuthorizationCookieValue(it) }

        return header ?: cookie
    }

    companion object {
        const val AUTHORIZATION_HEADER = "Authorization"
        const val COOKIE_HEADER = "Cookie"

        private const val WWW_AUTHENTICATE_HEADER = "WWW-Authenticate"
    }
}

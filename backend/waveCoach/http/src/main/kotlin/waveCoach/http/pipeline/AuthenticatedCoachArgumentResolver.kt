package waveCoach.http.pipeline

import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.MethodParameter
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import waveCoach.domain.AuthenticatedCoach

@Component
class AuthenticatedCoachArgumentResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter) = parameter.parameterType == AuthenticatedCoach::class.java

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): Any? {
        val request = webRequest.getNativeRequest(HttpServletRequest::class.java)
            ?: throw IllegalStateException("TODO")

        val user = AuthenticatedUserArgumentResolver.getUserFrom(request)
            ?: throw IllegalStateException("TODO")

        if (!user.info.isCoach) throw AccessDeniedException("TODO")

        return AuthenticatedCoach(user.info, user.token)
    }
}

package waveCoach.http.pipeline

import org.springframework.stereotype.Component
import waveCoach.domain.AuthenticatedUser
import waveCoach.services.UserServices
import waveCoach.utils.Failure
import waveCoach.utils.Success

@Component
class RequestTokenProcessor(
    val userServices: UserServices,
) {
    fun processAuthorizationHeaderValue(value: String?): AuthenticatedUser? {
        if (value == null) return null

        val parts = value.trim().split(" ")
        if (parts.size != 2 || parts[0].lowercase() != SCHEME) return null

        return validateToken(parts[1])
    }

    fun processAuthorizationCookieValue(value: String?): AuthenticatedUser? {
        if (value == null) return null

        val token =
            value
                .split("; ")
                .find { it.startsWith("$COOKIE=") }
                ?.substringAfter("$COOKIE=")
                ?: return null

        return validateToken(token)
    }

    private fun validateToken(token: String): AuthenticatedUser? =
        when (val result = userServices.getUserByToken(token)) {
            is Success -> AuthenticatedUser(result.value, token)
            is Failure -> null
        }

    companion object {
        const val SCHEME = "bearer"
        const val COOKIE = "token"
    }
}

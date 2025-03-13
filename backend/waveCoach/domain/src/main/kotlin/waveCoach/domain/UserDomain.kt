package waveCoach.domain

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.util.Base64

@Component
class UserDomain(
    private val passwordEncoder: PasswordEncoder,
    private val tokenEncoder: TokenEncoder,
    private val config: UserDomainConfig,
) {
    fun isUsernameValid(username: String): Boolean =
        username.length in 4..63

    fun isSafePassword(password: String): Boolean =
        password.length > 6 &&
                password.any { it.isUpperCase() } &&
                password.any { it.isLowerCase() } &&
                password.any { it.isDigit() } &&
                password.any { !it.isLetterOrDigit() }

    fun createPasswordValidationInformation(password: String) =
        PasswordValidationInfo(passwordEncoder.encode(password))

    fun validatePassword(password: String, validationInfo: PasswordValidationInfo) =
        passwordEncoder.matches(password, validationInfo.value)

    fun generateTokenValue(): String =
        ByteArray(config.tokenSizeInBytes).let { byteArray ->
            SecureRandom.getInstanceStrong().nextBytes(byteArray)
            Base64.getUrlEncoder().encodeToString(byteArray)
        }

    fun createTokenValidationInformation(token: String): TokenValidationInfo =
        tokenEncoder.createValidationInformation(token)

    fun canBeToken(token: String): Boolean =
        try {
            Base64.getUrlDecoder().decode(token).size == config.tokenSizeInBytes
        } catch (ex: IllegalArgumentException) {
            false
        }

    fun getTokenExpiration(token: Token): Instant {
        val absoluteExpiration = token.createdTime + config.tokenTtl
        val rollingExpiration = token.usedTime + config.tokenRollingTtl

        return when {
            absoluteExpiration < rollingExpiration -> absoluteExpiration
            else -> rollingExpiration
        }
    }

    fun isTokenTimeValid(
        clock: Clock,
        token: Token,
    ): Boolean {
        val now = clock.now()
        return token.createdTime <= now &&
            (now - token.createdTime) <= config.tokenTtl &&
            (now - token.usedTime) <= config.tokenRollingTtl
    }

    val maxNumberOfTokensPerUser = config.maxTokensPerUser
}
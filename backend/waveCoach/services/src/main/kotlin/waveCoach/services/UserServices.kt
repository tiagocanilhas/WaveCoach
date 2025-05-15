package waveCoach.services

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.springframework.stereotype.Component
import waveCoach.domain.Token
import waveCoach.domain.User
import waveCoach.domain.UserDomain
import waveCoach.repository.TransactionManager
import waveCoach.utils.Either
import waveCoach.utils.failure
import waveCoach.utils.success

data class LoginExternalInfo(
    val id: Int,
    val username: String,
    val isCoach: Boolean,
    val tokenValue: String,
    val tokenExpiration: Instant,
)

sealed class CheckCredentialsError {
    data object UsernameIsBlank : CheckCredentialsError()

    data object PasswordIsBlank : CheckCredentialsError()

    data object InvalidLogin : CheckCredentialsError()
}
typealias CheckCredentialsResult = Either<CheckCredentialsError, LoginExternalInfo>

sealed class GetUserByTokenError {
    data object InvalidToken : GetUserByTokenError()

    data object TokenNotFound : GetUserByTokenError()
}
typealias GetUserByTokenResult = Either<GetUserByTokenError, User>

sealed class RevokeTokenError {
    data object InvalidToken : RevokeTokenError()

    data object TokenNotFound : RevokeTokenError()
}
typealias RevokeTokenResult = Either<RevokeTokenError, Boolean>

sealed class UserUpdateError {
    data object InvalidUsername : UserUpdateError()

    data object InsecurePassword : UserUpdateError()

    data object UsernameAlreadyExists : UserUpdateError()
}
typealias UserUpdateResult = Either<UserUpdateError, Boolean>

@Component
class UserServices(
    private val transactionManager: TransactionManager,
    private val userDomain: UserDomain,
    private val clock: Clock,
) {
    fun checkCredentials(
        username: String,
        password: String,
    ): CheckCredentialsResult {
        if (username.isBlank()) return failure(CheckCredentialsError.UsernameIsBlank)
        if (password.isBlank()) return failure(CheckCredentialsError.PasswordIsBlank)

        return transactionManager.run {
            val userRepository = it.userRepository

            val user = userRepository.getUserByUsername(username) ?: return@run failure(CheckCredentialsError.InvalidLogin)
            if (!userDomain.validatePassword(password, user.password)) {
                return@run failure(CheckCredentialsError.InvalidLogin)
            }

            val value = userDomain.generateTokenValue()
            val tokenValidationInfo = userDomain.createTokenValidationInformation(value)
            val now = clock.now()
            val token = Token(tokenValidationInfo, user.id, now, now)
            userRepository.storeToken(token, userDomain.maxNumberOfTokensPerUser)

            success(LoginExternalInfo(user.id, user.username, user.isCoach, value, userDomain.getTokenExpiration(token)))
        }
    }

    fun getUserByToken(tokenReceived: String): GetUserByTokenResult {
        if (!userDomain.canBeToken(tokenReceived)) return failure(GetUserByTokenError.InvalidToken)

        return transactionManager.run {
            val userRepository = it.userRepository

            val tokenValidationInfo = userDomain.createTokenValidationInformation(tokenReceived)
            val (user, token) =
                userRepository.getToken(tokenValidationInfo)
                    ?: return@run failure(GetUserByTokenError.TokenNotFound)

            userRepository.updateTokenLastUsed(token, clock.now())

            if (!userDomain.isTokenTimeValid(clock, token)) return@run failure(GetUserByTokenError.InvalidToken)
            userRepository.updateTokenLastUsed(token, clock.now())
            success(user)
        }
    }

    fun revokeToken(token: String): RevokeTokenResult {
        if (!userDomain.canBeToken(token)) return failure(RevokeTokenError.InvalidToken)
        val tokenValidationInfo = userDomain.createTokenValidationInformation(token)

        return transactionManager.run {
            val userRepository = it.userRepository

            userRepository.getToken(tokenValidationInfo) ?: return@run failure(RevokeTokenError.TokenNotFound)

            userRepository.removeToken(tokenValidationInfo)
            success(true)
        }
    }

    fun updateCredentials(
        userId: Int,
        newUsername: String,
        newPassword: String,
    ): UserUpdateResult {
        if (!userDomain.isUsernameValid(newUsername)) return failure(UserUpdateError.InvalidUsername)
        if (!userDomain.isSafePassword(newPassword)) return failure(UserUpdateError.InsecurePassword)

        return transactionManager.run {
            val userRepository = it.userRepository

            if (userRepository.checkUsername(newUsername)) return@run failure(UserUpdateError.UsernameAlreadyExists)

            val passwordValidationInfo = userDomain.createPasswordValidationInformation(newPassword)
            userRepository.updateUser(userId, newUsername, passwordValidationInfo)
            success(true)
        }
    }
}

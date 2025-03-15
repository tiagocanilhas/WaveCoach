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
    val tokenValue: String,
    val tokenExpiration: Instant,
)

sealed class CreateUserError {
    data object InsecurePassword : CreateUserError()
    data object UsernameAlreadyExists : CreateUserError()
}
typealias CreateUserResult = Either<CreateUserError, Int>

sealed class LoginError {
    data object UsernameIsBlank : LoginError()
    data object PasswordIsBlank : LoginError()
    data object InvalidLogin : LoginError()
}
typealias LoginResult = Either<LoginError, LoginExternalInfo>

sealed class GetUserByTokenError {
    data object InvalidToken : GetUserByTokenError()
    data object TokenNotFound : GetUserByTokenError()
}
typealias GetUserByTokenResult = Either<GetUserByTokenError, User>



@Component
class UserServices(
    private val transactionManager: TransactionManager,
    private val userDomain: UserDomain,
    private val clock: Clock,
){
    fun createUser(
        username: String,
        password: String
    ): CreateUserResult {
        if (!userDomain.isSafePassword(password)) return failure(CreateUserError.InsecurePassword)

        val passwordValidationInfo = userDomain.createPasswordValidationInformation(password)

        return transactionManager.run {
            val userRepository = it.userRepository

            if (userRepository.checkUsername(username)) return@run failure(CreateUserError.UsernameAlreadyExists)

            success(userRepository.storeUser(username, passwordValidationInfo))
        }
    }

    fun login(
        username: String,
        password: String
    ): LoginResult {
        if (username.isBlank()) return failure(LoginError.UsernameIsBlank)
        if (password.isBlank()) return failure(LoginError.PasswordIsBlank)

        return transactionManager.run {
            val userRepository = it.userRepository

            val user = userRepository.getUserByUsername(username) ?: return@run failure(LoginError.InvalidLogin)
            if (!userDomain.validatePassword(password, user.password))
                return@run failure(LoginError.InvalidLogin)

            val value = userDomain.generateTokenValue()
            val tokenValidationInfo = userDomain.createTokenValidationInformation(value)
            val now = clock.now()
            val token = Token(tokenValidationInfo, user.id, now, now)
            userRepository.storeToken(token, userDomain.maxNumberOfTokensPerUser)

            success(LoginExternalInfo(user.id, user.username, value, userDomain.getTokenExpiration(token)))
        }
    }

    fun getUserByToken(tokenReceived: String): GetUserByTokenResult {
        if (!userDomain.canBeToken(tokenReceived)) return failure(GetUserByTokenError.InvalidToken)

        return transactionManager.run {
            val userRepository = it.userRepository

            val tokenValidationInfo = userDomain.createTokenValidationInformation(tokenReceived)
            val (user, token) = userRepository.getToken(tokenValidationInfo)
                ?: return@run failure(GetUserByTokenError.TokenNotFound)

            userRepository.updateTokenLastUsed(token, clock.now())

            if (!userDomain.isTokenTimeValid(clock, token)) return@run failure(GetUserByTokenError.InvalidToken)
            userRepository.updateTokenLastUsed(token, clock.now())
            success(user)
        }
    }
}
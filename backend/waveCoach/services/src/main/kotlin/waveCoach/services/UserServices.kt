package waveCoach.services

import kotlinx.datetime.Clock
import org.springframework.stereotype.Component
import waveCoach.domain.User
import waveCoach.domain.UserDomain
import waveCoach.repository.TransactionManager
import waveCoach.utils.Either
import waveCoach.utils.failure
import waveCoach.utils.success

sealed class CreateUserError {
    data object InsecurePassword : CreateUserError()
    data object UsernameAlreadyExists : CreateUserError()
}
typealias CreateUserResult = Either<CreateUserError, Int>

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
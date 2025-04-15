package waveCoach.services

import org.springframework.stereotype.Component
import waveCoach.domain.UserDomain
import waveCoach.repository.TransactionManager
import waveCoach.utils.Either
import waveCoach.utils.failure
import waveCoach.utils.success

sealed class CreateCoachError {
    data object InvalidUsername : CreateCoachError()

    data object InsecurePassword : CreateCoachError()

    data object UsernameAlreadyExists : CreateCoachError()
}
typealias CreateCoachResult = Either<CreateCoachError, Int>

@Component
class CoachServices(
    private val transactionManager: TransactionManager,
    private val userDomain: UserDomain,
) {
    fun createCoach(
        username: String,
        password: String,
    ): CreateCoachResult {
        if (!userDomain.isUsernameValid(username)) return failure(CreateCoachError.InvalidUsername)
        if (!userDomain.isSafePassword(password)) return failure(CreateCoachError.InsecurePassword)

        val passwordValidationInfo = userDomain.createPasswordValidationInformation(password)

        return transactionManager.run {
            val userRepository = it.userRepository
            val coachRepository = it.coachRepository

            if (userRepository.checkUsername(username)) return@run failure(CreateCoachError.UsernameAlreadyExists)

            val uid = userRepository.storeUser(username, passwordValidationInfo)
            coachRepository.storeCoach(uid)
            success(uid)
        }
    }
}

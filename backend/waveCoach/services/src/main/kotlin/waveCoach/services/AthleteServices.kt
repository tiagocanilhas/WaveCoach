package waveCoach.services

import org.springframework.stereotype.Component
import waveCoach.domain.AthleteDomain
import waveCoach.domain.UserDomain
import waveCoach.repository.TransactionManager
import waveCoach.utils.Either
import waveCoach.utils.failure
import waveCoach.utils.success

sealed class CreateAthleteError {
    data object InvalidBirthDate : CreateAthleteError()
    data object InvalidName : CreateAthleteError()
}
typealias CreateAthleteResult = Either<CreateAthleteError, Int>

@Component
class AthleteServices(
    private val transactionManager: TransactionManager,
    private val athleteDomain: AthleteDomain,
    private val userDomain: UserDomain,
) {
    fun createAthlete(
        name: String,
        coachId: Int,
        birthDate: String
    ): CreateAthleteResult {
        val passwordValidationInfo = userDomain.createPasswordValidationInformation("changeit")

        if (athleteDomain.birthDateValid(birthDate)) return failure(CreateAthleteError.InvalidBirthDate)
        if(!athleteDomain.nameValid(name)) return failure(CreateAthleteError.InvalidName)

        return transactionManager.run {
            val userRepository = it.userRepository
            val athleteRepository = it.athleteRepository

            val aid = userRepository.storeUser("athlete", passwordValidationInfo)
            athleteRepository.storeAthlete(aid, coachId, name, birthDate)
            success(aid)
        }
    }
}
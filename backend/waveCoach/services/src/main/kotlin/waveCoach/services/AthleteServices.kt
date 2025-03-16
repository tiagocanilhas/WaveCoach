package waveCoach.services

import org.springframework.stereotype.Component
import waveCoach.domain.AthleteDomain
import waveCoach.domain.UserDomain
import waveCoach.repository.TransactionManager
import waveCoach.utils.Either
import waveCoach.utils.failure
import waveCoach.utils.success
import kotlin.math.abs
import kotlin.random.Random

sealed class CreateAthleteError {
    data object InvalidBirthDate : CreateAthleteError()
    data object InvalidName : CreateAthleteError()
}
typealias CreateAthleteResult = Either<CreateAthleteError, Int>

sealed class RemoveAthleteError {
    data object AthleteNotFound : RemoveAthleteError()
    data object NotAthletesCoach : RemoveAthleteError()
}
typealias RemoveAthleteResult = Either<RemoveAthleteError, Int>

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

        val date = athleteDomain.birthDateToLong(birthDate) ?: return failure(CreateAthleteError.InvalidBirthDate)

        if (!athleteDomain.nameValid(name)) return failure(CreateAthleteError.InvalidName)

            return transactionManager.run {
                val userRepository = it.userRepository
                val athleteRepository = it.athleteRepository

                val aid = userRepository.storeUser("Athlete_${abs(Random.nextLong())}", passwordValidationInfo)
                athleteRepository.storeAthlete(aid, coachId, name, date)
                success(aid)
            }
    }

    fun removeAthlete(coachId: Int, aid: Int): RemoveAthleteResult {
        return transactionManager.run {
            val athleteRepository = it.athleteRepository
            val userRepository = it.userRepository

            val athlete = athleteRepository.getAthlete(aid) ?: return@run failure(RemoveAthleteError.AthleteNotFound)
            if (athlete.coach != coachId) return@run failure(RemoveAthleteError.NotAthletesCoach)

            athleteRepository.removeAthlete(aid)
            userRepository.removeUser(aid)

            success(aid)
        }
    }
}
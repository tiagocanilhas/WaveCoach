package waveCoach.services

import org.springframework.stereotype.Component
import waveCoach.domain.AthleteDomain
import waveCoach.domain.CharacteristicsDomain
import waveCoach.domain.UserDomain
import waveCoach.repository.TransactionManager
import waveCoach.utils.Either
import waveCoach.utils.failure
import waveCoach.utils.success
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

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

sealed class CreateCharacteristicsError {
    data object InvalidDate : CreateCharacteristicsError()
    data object InvalidCharacteristics : CreateCharacteristicsError()
    data object AthleteNotFound : CreateCharacteristicsError()
    data object NotAthletesCoach : CreateCharacteristicsError()
}
typealias CreateCharacteristicsResult = Either<CreateCharacteristicsError, Int>

sealed class UpdateCharacteristicsError {
    data object InvalidDate : UpdateCharacteristicsError()
    data object InvalidCharacteristics : UpdateCharacteristicsError()
    data object AthleteNotFound : UpdateCharacteristicsError()
    data object NotAthletesCoach : UpdateCharacteristicsError()
}
typealias UpdateCharacteristicsResult = Either<UpdateCharacteristicsError, Int>

@Component
class AthleteServices(
    private val transactionManager: TransactionManager,
    private val athleteDomain: AthleteDomain,
    private val characteristicsDomain: CharacteristicsDomain,
    private val userDomain: UserDomain,
) {
    fun createAthlete(
        name: String,
        coachId: Int,
        birthDate: String
    ): CreateAthleteResult {
        val username = athleteDomain.createAthleteUsername()
        val passwordValidationInfo =
            userDomain.createPasswordValidationInformation(athleteDomain.athleteDefaultPassword)

        if (!athleteDomain.isNameValid(name)) return failure(CreateAthleteError.InvalidName)
        val date = dateToLong(birthDate) ?: return failure(CreateAthleteError.InvalidBirthDate)

        return transactionManager.run {
            val userRepository = it.userRepository
            val athleteRepository = it.athleteRepository

            val aid = userRepository.storeUser(username, passwordValidationInfo)
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

    fun createCharacteristics(
        coachId: Int,
        uid: Int,
        date: String?,
        height: Int?,
        weight: Float?,
        calories: Int?,
        waist: Int?,
        arm: Int?,
        thigh: Int?,
        tricep: Float?,
        abdominal: Float?
    ): CreateCharacteristicsResult {
        val dateLong = date?.let { dateToLong(it) }
            ?: if (date != null) return failure(CreateCharacteristicsError.InvalidDate) else null

        if (!characteristicsDomain.checkCharacteristics(height, weight, calories, waist, arm, thigh, tricep, abdominal))
            return failure(CreateCharacteristicsError.InvalidCharacteristics)

        return transactionManager.run {
            val characteristicsRepository = it.characteristicsRepository
            val athleteRepository = it.athleteRepository

            val athlete =
                athleteRepository.getAthlete(uid) ?: return@run failure(CreateCharacteristicsError.AthleteNotFound)
            if (athlete.coach != coachId) return@run failure(CreateCharacteristicsError.NotAthletesCoach)

            if (dateLong == null)
                characteristicsRepository.storeCharacteristicsWithoutDate(
                    uid,
                    height,
                    weight,
                    calories,
                    waist,
                    arm,
                    thigh,
                    tricep,
                    abdominal
                )
            else
                characteristicsRepository.storeCharacteristics(
                    uid,
                    dateLong,
                    height,
                    weight,
                    calories,
                    waist,
                    arm,
                    thigh,
                    tricep,
                    abdominal
                )

            success(uid)
        }
    }

    fun updateCharacteristics(
        coachId: Int,
        uid: Int,
        date: String,
        height: Int?,
        weight: Float?,
        calories: Int?,
        waist: Int?,
        arm: Int?,
        thigh: Int?,
        tricep: Float?,
        abdominal: Float?
    ): UpdateCharacteristicsResult {
        val dateLong = dateToLong(date) ?: return failure(UpdateCharacteristicsError.InvalidDate)

        if (!characteristicsDomain.checkCharacteristics(height, weight, calories, waist, arm, thigh, tricep, abdominal))
            return failure(UpdateCharacteristicsError.InvalidCharacteristics)

        return transactionManager.run {
            val characteristicsRepository = it.characteristicsRepository
            val athleteRepository = it.athleteRepository

            val athlete =
                athleteRepository.getAthlete(uid) ?: return@run failure(UpdateCharacteristicsError.AthleteNotFound)
            if (athlete.coach != coachId) return@run failure(UpdateCharacteristicsError.NotAthletesCoach)

            characteristicsRepository.updateCharacteristics(
                uid,
                dateLong,
                height,
                weight,
                calories,
                waist,
                arm,
                thigh,
                tricep,
                abdominal
            )
            success(uid)
        }
    }

    private fun dateToLong(birthDate: String): Long? {
        return try {
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            val date = LocalDate.parse(birthDate, formatter)

            if (date.isBefore(LocalDate.now())) {
                date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            } else {
                null
            }
        } catch (e: DateTimeParseException) {
            null
        }
    }
}
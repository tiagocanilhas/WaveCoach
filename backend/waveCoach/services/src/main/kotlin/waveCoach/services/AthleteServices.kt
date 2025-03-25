package waveCoach.services

import org.springframework.stereotype.Component
import waveCoach.domain.AthleteDomain
import waveCoach.domain.Characteristics
import waveCoach.domain.CharacteristicsDomain
import waveCoach.domain.UserDomain
import waveCoach.domain.Athlete
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

sealed class GetAthleteError {
    data object AthleteNotFound : GetAthleteError()
    data object NotAthletesCoach : GetAthleteError()
}
typealias GetAthleteResult = Either<GetAthleteError, Athlete>

sealed class UpdateAthleteError {
    data object InvalidBirthDate : UpdateAthleteError()
    data object InvalidName : UpdateAthleteError()
    data object AthleteNotFound : UpdateAthleteError()
    data object NotAthletesCoach : UpdateAthleteError()
}
typealias UpdateAthleteResult = Either<UpdateAthleteError, Int>

sealed class RemoveAthleteError {
    data object AthleteNotFound : RemoveAthleteError()
    data object NotAthletesCoach : RemoveAthleteError()
}
typealias RemoveAthleteResult = Either<RemoveAthleteError, Int>

sealed class CreateCharacteristicsError {
    data object InvalidDate : CreateCharacteristicsError()
    data object CharacteristicsAlreadyExists : CreateCharacteristicsError()
    data object InvalidCharacteristics : CreateCharacteristicsError()
    data object AthleteNotFound : CreateCharacteristicsError()
    data object NotAthletesCoach : CreateCharacteristicsError()
}
typealias CreateCharacteristicsResult = Either<CreateCharacteristicsError, Long>

sealed class GetCharacteristicsError {
    data object InvalidDate : GetCharacteristicsError()
    data object AthleteNotFound : GetCharacteristicsError()
    data object NotAthletesCoach : GetCharacteristicsError()
    data object CharacteristicsNotFound : GetCharacteristicsError()
}
typealias GetCharacteristicsResult = Either<GetCharacteristicsError, Characteristics>

sealed class GetCharacteristicsListError {
    data object AthleteNotFound : GetCharacteristicsListError()
    data object NotAthletesCoach : GetCharacteristicsListError()
}
typealias GetCharacteristicsListResult = Either<GetCharacteristicsListError, List<Characteristics>>

sealed class UpdateCharacteristicsError {
    data object InvalidDate : UpdateCharacteristicsError()
    data object InvalidCharacteristics : UpdateCharacteristicsError()
    data object AthleteNotFound : UpdateCharacteristicsError()
    data object NotAthletesCoach : UpdateCharacteristicsError()
}
typealias UpdateCharacteristicsResult = Either<UpdateCharacteristicsError, Int>

sealed class RemoveCharacteristicsError {
    data object InvalidDate : RemoveCharacteristicsError()
    data object AthleteNotFound : RemoveCharacteristicsError()
    data object CharacteristicsNotFound : RemoveCharacteristicsError()
    data object NotAthletesCoach : RemoveCharacteristicsError()
}
typealias RemoveCharacteristicsResult = Either<RemoveCharacteristicsError, Int>

@Component
class AthleteServices(
    private val transactionManager: TransactionManager,
    private val athleteDomain: AthleteDomain,
    private val characteristicsDomain: CharacteristicsDomain,
    private val userDomain: UserDomain
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

    fun getAthlete(coachId: Int, aid: Int): GetAthleteResult {
        return transactionManager.run {
            val athleteRepository = it.athleteRepository

            val athlete = athleteRepository.getAthlete(aid) ?: return@run failure(GetAthleteError.AthleteNotFound)
            if (athlete.coach != coachId) return@run failure(GetAthleteError.NotAthletesCoach)

            success(athlete)
        }
    }

    fun getAthletes(coachId: Int): List<Athlete> {
        return transactionManager.run {
            val athleteRepository = it.athleteRepository

            athleteRepository.getAthleteList(coachId)
        }
    }

    fun updateAthlete(
        coachId: Int,
        aid: Int,
        name: String,
        birthDate: String
    ): UpdateAthleteResult {
        val date = dateToLong(birthDate) ?: return failure(UpdateAthleteError.InvalidBirthDate)

        if (!athleteDomain.isNameValid(name)) return failure(UpdateAthleteError.InvalidName)

        return transactionManager.run {
            val athleteRepository = it.athleteRepository

            val athlete = athleteRepository.getAthlete(aid) ?: return@run failure(UpdateAthleteError.AthleteNotFound)
            if (athlete.coach != coachId) return@run failure(UpdateAthleteError.NotAthletesCoach)

            athleteRepository.updateAthlete(aid, name, date)
            success(aid)
        }
    }

    fun removeAthlete(coachId: Int, aid: Int): RemoveAthleteResult {
        return transactionManager.run {
            val athleteRepository = it.athleteRepository
            val userRepository = it.userRepository
            val characteristicsRepository = it.characteristicsRepository

            val athlete = athleteRepository.getAthlete(aid) ?: return@run failure(RemoveAthleteError.AthleteNotFound)
            if (athlete.coach != coachId) return@run failure(RemoveAthleteError.NotAthletesCoach)

            characteristicsRepository.removeCharacteristicsWithoutDate(aid)

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
        bodyFat: Float?,
        waistSize: Int?,
        armSize: Int?,
        thighSize: Int?,
        tricepFat: Int?,
        abdomenFat: Int?,
        thighFat: Int?
    ): CreateCharacteristicsResult {
        val dateLong = date?.let { dateToLong(it) }
            ?: if (date != null) return failure(CreateCharacteristicsError.InvalidDate) else null

        if (!characteristicsDomain.checkCharacteristics(
                height,
                weight,
                calories,
                bodyFat,
                waistSize,
                armSize,
                thighSize,
                tricepFat,
                abdomenFat,
                thighFat
            )
        )
            return failure(CreateCharacteristicsError.InvalidCharacteristics)

        return transactionManager.run {
            val characteristicsRepository = it.characteristicsRepository
            val athleteRepository = it.athleteRepository

            val athlete =
                athleteRepository.getAthlete(uid) ?: return@run failure(CreateCharacteristicsError.AthleteNotFound)

            if (athlete.coach != coachId) return@run failure(CreateCharacteristicsError.NotAthletesCoach)

            val characteristicsId =
                if (dateLong == null)
                    characteristicsRepository.storeCharacteristicsWithoutDate(
                        uid,
                        height,
                        weight,
                        calories,
                        bodyFat,
                        waistSize,
                        armSize,
                        thighSize,
                        tricepFat,
                        abdomenFat,
                        thighFat
                    )
                else {
                    characteristicsRepository.getCharacteristics(uid, dateLong)
                        ?.let { return@run failure(CreateCharacteristicsError.CharacteristicsAlreadyExists) }

                    characteristicsRepository.storeCharacteristics(
                        uid,
                        dateLong,
                        height,
                        weight,
                        calories,
                        bodyFat,
                        waistSize,
                        armSize,
                        thighSize,
                        tricepFat,
                        abdomenFat,
                        thighFat
                    )
                }

            success(characteristicsId)
        }
    }

    fun getCharacteristics(coachId: Int, uid: Int, date: String): GetCharacteristicsResult {
        val dateLong = dateToLong(date) ?: return failure(GetCharacteristicsError.InvalidDate)

        return transactionManager.run {
            val characteristicsRepository = it.characteristicsRepository
            val athleteRepository = it.athleteRepository

            val athlete =
                athleteRepository.getAthlete(uid) ?: return@run failure(GetCharacteristicsError.AthleteNotFound)

            if (athlete.coach != coachId) return@run failure(GetCharacteristicsError.NotAthletesCoach)

            val characteristics = characteristicsRepository.getCharacteristics(uid, dateLong)
                ?: return@run failure(GetCharacteristicsError.CharacteristicsNotFound)

            success(characteristics)
        }
    }

    fun getCharacteristicsList(coachId: Int, uid: Int): GetCharacteristicsListResult {
        return transactionManager.run {
            val characteristicsRepository = it.characteristicsRepository
            val athleteRepository = it.athleteRepository

            val athlete =
                athleteRepository.getAthlete(uid) ?: return@run failure(GetCharacteristicsListError.AthleteNotFound)

            if (athlete.coach != coachId) return@run failure(GetCharacteristicsListError.NotAthletesCoach)

            val characteristicsList = characteristicsRepository.getCharacteristicsList(uid)
            success(characteristicsList)
        }
    }

    fun updateCharacteristics(
        coachId: Int,
        uid: Int,
        date: String,
        height: Int?,
        weight: Float?,
        calories: Int?,
        bodyFat: Float?,
        waistSize: Int?,
        armSize: Int?,
        thighSize: Int?,
        tricepFat: Int?,
        abdomenFat: Int?,
        thighFat: Int?
    ): UpdateCharacteristicsResult {
        val dateLong = dateToLong(date) ?: return failure(UpdateCharacteristicsError.InvalidDate)

        if (!characteristicsDomain.checkCharacteristics(
                height,
                weight,
                calories,
                bodyFat,
                waistSize,
                armSize,
                thighSize,
                tricepFat,
                abdomenFat,
                thighFat
            )
        )
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
                bodyFat,
                waistSize,
                armSize,
                thighSize,
                tricepFat,
                abdomenFat,
                thighFat
            )
            success(uid)
        }
    }

    fun removeCharacteristics(coachId: Int, uid: Int, date: String): RemoveCharacteristicsResult {
        val dateLong = dateToLong(date) ?: return failure(RemoveCharacteristicsError.InvalidDate)

        return transactionManager.run {
            val characteristicsRepository = it.characteristicsRepository
            val athleteRepository = it.athleteRepository

            val athlete =
                athleteRepository.getAthlete(uid) ?: return@run failure(RemoveCharacteristicsError.AthleteNotFound)

            if (athlete.coach != coachId) return@run failure(RemoveCharacteristicsError.NotAthletesCoach)

            characteristicsRepository.getCharacteristics(uid, dateLong)
                ?: return@run failure(RemoveCharacteristicsError.CharacteristicsNotFound)

            characteristicsRepository.removeCharacteristics(uid, dateLong)
            success(uid)
        }
    }

    private fun dateToLong(date: String): Long? {
        return try {
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            val dateParsed = LocalDate.parse(date, formatter)

            if (dateParsed.isBefore(LocalDate.now())) {
                dateParsed.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            } else {
                null
            }
        } catch (e: DateTimeParseException) {
            null
        }
    }
}
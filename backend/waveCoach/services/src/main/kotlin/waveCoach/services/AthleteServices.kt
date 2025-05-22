package waveCoach.services

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import waveCoach.domain.Activity
import waveCoach.domain.ActivityDomain
import waveCoach.domain.Athlete
import waveCoach.domain.AthleteCode
import waveCoach.domain.AthleteDomain
import waveCoach.domain.Characteristics
import waveCoach.domain.CharacteristicsDomain
import waveCoach.domain.Mesocycle
import waveCoach.domain.UserDomain
import waveCoach.repository.TransactionManager
import waveCoach.utils.Either
import waveCoach.utils.failure
import waveCoach.utils.success
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

data class AthleteCodeExternalInfo(
    val code: String,
    val expirationDate: Instant,
)

data class MesocycleInputInfo(
    val id: Int?,
    val startTime: Long,
    val endTime: Long,
    val microcycles: List<MicrocycleInputInfo>,
    )

data class MicrocycleInputInfo(
    val id: Int?,
    val startTime: Long,
    val endTime: Long,
)

sealed class CreateAthleteError {
    data object InvalidBirthDate : CreateAthleteError()

    data object InvalidName : CreateAthleteError()

    data object InvalidPhoto : CreateAthleteError()
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

sealed class GenerateCodeError {
    data object AthleteNotFound : GenerateCodeError()

    data object NotAthletesCoach : GenerateCodeError()

    data object CredentialsAlreadyChanged : GenerateCodeError()
}
typealias GenerateCodeResult = Either<GenerateCodeError, AthleteCodeExternalInfo>

sealed class GetUsernameByCodeError {
    data object InvalidCode : GetUsernameByCodeError()
}
typealias GetUsernameByCodeResult = Either<GetUsernameByCodeError, String>

sealed class ChangeCredentialsError {
    data object InvalidCode : ChangeCredentialsError()

    data object InvalidUsername : ChangeCredentialsError()

    data object UsernameAlreadyExists : ChangeCredentialsError()

    data object InsecurePassword : ChangeCredentialsError()
}
typealias ChangeAthleteCredentialsResult = Either<ChangeCredentialsError, Int>

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

sealed class GetCalendarError{
    data object AthleteNotFound : GetCalendarError()

    data object NotAthletesCoach : GetCalendarError()
}
typealias GetCalendarResult = Either<GetCalendarError, List<Mesocycle>>

sealed class SetCalendarError {
    data object MesocycleNotFound : SetCalendarError()

    data object InvalidMesocycle : SetCalendarError()

    data object MicrocycleNotFound : SetCalendarError()

    data object InvalidMicrocycle : SetCalendarError()

    data object AthleteNotFound : SetCalendarError()

    data object NotAthletesCoach : SetCalendarError()
}
typealias SetCalendarResult = Either<SetCalendarError, Boolean>

sealed class GetActivitiesError {
    data object AthleteNotFound : GetActivitiesError()

    data object NotAthletesCoach : GetActivitiesError()
}
typealias GetActivitiesResult = Either<GetActivitiesError, List<Activity>>

@Component
class AthleteServices(
    private val transactionManager: TransactionManager,
    private val athleteDomain: AthleteDomain,
    private val characteristicsDomain: CharacteristicsDomain,
    private val userDomain: UserDomain,
    private val activityDomain: ActivityDomain,
    private val cloudinaryServices: CloudinaryServices,
    private val clock: Clock,
) {
    fun createAthlete(
        name: String,
        coachId: Int,
        birthDate: String,
        photo: MultipartFile?
    ): CreateAthleteResult {
        val username = athleteDomain.createAthleteUsername()
        val passwordValidationInfo =
            userDomain.createPasswordValidationInformation(athleteDomain.athleteDefaultPassword)

        if (!athleteDomain.isNameValid(name)) return failure(CreateAthleteError.InvalidName)
        val date = dateToLong(birthDate) ?: return failure(CreateAthleteError.InvalidBirthDate)

        val url = photo?.let {
            cloudinaryServices.uploadAthleteImage(it)
                ?: return failure(CreateAthleteError.InvalidPhoto)
        }

        return transactionManager.run {
            val userRepository = it.userRepository
            val athleteRepository = it.athleteRepository

            val aid = userRepository.storeUser(username, passwordValidationInfo)
            athleteRepository.storeAthlete(aid, coachId, name, date, url)
            success(aid)
        }
    }

    fun getAthlete(
        uid: Int,
        aid: Int,
    ): GetAthleteResult {
        return transactionManager.run {
            val athleteRepository = it.athleteRepository

            val athlete = athleteRepository.getAthlete(aid) ?: return@run failure(GetAthleteError.AthleteNotFound)

            when {
                uid == aid -> success(athlete)
                athlete.coach != uid -> failure(GetAthleteError.NotAthletesCoach)
                else -> success(athlete)
            }
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
        birthDate: String,
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

    fun removeAthlete(
        coachId: Int,
        aid: Int,
    ): RemoveAthleteResult {
        return transactionManager.run {
            val athleteRepository = it.athleteRepository
            val userRepository = it.userRepository
            val characteristicsRepository = it.characteristicsRepository
            val activityRepository = it.activityRepository
            val gymActivityRepository = it.gymActivityRepository

            val athlete = athleteRepository.getAthlete(aid) ?: return@run failure(RemoveAthleteError.AthleteNotFound)
            if (athlete.coach != coachId) return@run failure(RemoveAthleteError.NotAthletesCoach)

            characteristicsRepository.removeCharacteristicsWithoutDate(aid)
            gymActivityRepository.removeSetsByAthlete(aid)
            gymActivityRepository.removeExercisesByAthlete(aid)
            gymActivityRepository.removeGymActivities(aid)
            activityRepository.removeActivities(aid)
            athleteRepository.removeCode(aid)
            athleteRepository.removeAthlete(aid)
            userRepository.removeUser(aid)

            success(aid)
        }
    }

    fun generateCode(
        cid: Int,
        aid: Int,
    ): GenerateCodeResult {
        return transactionManager.run {
            val athleteRepository = it.athleteRepository

            val athlete = athleteRepository.getAthlete(aid) ?: return@run failure(GenerateCodeError.AthleteNotFound)
            if (athlete.coach != cid) return@run failure(GenerateCodeError.NotAthletesCoach)
            if (athlete.credentialsChanged) return@run failure(GenerateCodeError.CredentialsAlreadyChanged)

            val value = userDomain.generateTokenValue()
            val codeValidationInfo = athleteDomain.createAthleteCodeValidationInformation(value)
            val now = clock.now()
            val code = AthleteCode(aid, codeValidationInfo, now)
            athleteRepository.storeCode(code)

            success(AthleteCodeExternalInfo(value, athleteDomain.getCodeExpiration(now)))
        }
    }

    fun getUsernameByCode(code: String): GetUsernameByCodeResult {
        return transactionManager.run {
            val athleteRepository = it.athleteRepository

            val codeValidationInfo = athleteDomain.createAthleteCodeValidationInformation(code)
            val (athlete, username, createdTime) =
                athleteRepository.getByCode(codeValidationInfo)
                    ?: return@run failure(GetUsernameByCodeError.InvalidCode)
            val expirationDate = athleteDomain.getCodeExpiration(Instant.fromEpochSeconds(createdTime))
            if (clock.now() > expirationDate) {
                athleteRepository.removeCode(athlete.uid)
                return@run failure(GetUsernameByCodeError.InvalidCode)
            }

            success(username)
        }
    }

    fun changeCredentials(
        code: String,
        username: String,
        password: String,
    ): ChangeAthleteCredentialsResult {
        if (!userDomain.isUsernameValid(username)) return failure(ChangeCredentialsError.InvalidUsername)
        if (!userDomain.isSafePassword(password)) return failure(ChangeCredentialsError.InsecurePassword)

        val passwordValidationInfo = userDomain.createPasswordValidationInformation(password)

        return transactionManager.run {
            val userRepository = it.userRepository
            val athleteRepository = it.athleteRepository

            val codeValidationInfo = athleteDomain.createAthleteCodeValidationInformation(code)
            val (athlete, _, createdTime) =
                athleteRepository.getByCode(codeValidationInfo)
                    ?: return@run failure(ChangeCredentialsError.InvalidCode)
            val expirationDate = athleteDomain.getCodeExpiration(Instant.fromEpochSeconds(createdTime))
            if (clock.now() > expirationDate) {
                athleteRepository.removeCode(athlete.uid)
                return@run failure(ChangeCredentialsError.InvalidCode)
            }
            if (userRepository.checkUsername(username)) return@run failure(ChangeCredentialsError.UsernameAlreadyExists)

            userRepository.updateUser(athlete.uid, username, passwordValidationInfo)
            athleteRepository.setCredentialsChangedToTrue(athlete.uid)
            athleteRepository.removeCode(athlete.uid)
            success(athlete.uid)
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
        thighFat: Int?,
    ): CreateCharacteristicsResult {
        val dateLong =
            date?.let { dateToLong(it) }
                ?: if (date != null) return failure(CreateCharacteristicsError.InvalidDate) else null

        if (!characteristicsDomain.checkCharacteristics(
                height, weight, calories, bodyFat, waistSize,
                armSize, thighSize, tricepFat, abdomenFat, thighFat,
            )
        ) {
            return failure(CreateCharacteristicsError.InvalidCharacteristics)
        }

        return transactionManager.run {
            val characteristicsRepository = it.characteristicsRepository
            val athleteRepository = it.athleteRepository

            val athlete =
                athleteRepository.getAthlete(uid) ?: return@run failure(CreateCharacteristicsError.AthleteNotFound)

            if (athlete.coach != coachId) return@run failure(CreateCharacteristicsError.NotAthletesCoach)

            val characteristicsId =
                if (dateLong == null) {
                    characteristicsRepository.storeCharacteristicsWithoutDate(
                        uid, height, weight, calories, bodyFat, waistSize, armSize,
                        thighSize, tricepFat, abdomenFat, thighFat,
                    )
                } else {
                    characteristicsRepository.getCharacteristics(uid, dateLong)
                        ?.let { return@run failure(CreateCharacteristicsError.CharacteristicsAlreadyExists) }

                    characteristicsRepository.storeCharacteristics(
                        uid, dateLong, height, weight, calories, bodyFat, waistSize,
                        armSize, thighSize, tricepFat, abdomenFat, thighFat,
                    )
                }

            success(characteristicsId)
        }
    }

    fun getCharacteristics(
        uid: Int,
        aid: Int,
        date: String,
    ): GetCharacteristicsResult {
        val dateLong = dateToLong(date) ?: return failure(GetCharacteristicsError.InvalidDate)

        return transactionManager.run {
            val characteristicsRepository = it.characteristicsRepository
            val athleteRepository = it.athleteRepository

            val athlete = athleteRepository.getAthlete(aid)
                ?: return@run failure(GetCharacteristicsError.AthleteNotFound)

            if (uid != aid && uid != athlete.coach) return@run failure(GetCharacteristicsError.NotAthletesCoach)

            val characteristics = characteristicsRepository.getCharacteristics(aid, dateLong)
                ?: return@run failure(GetCharacteristicsError.CharacteristicsNotFound)

            success(characteristics)
        }
    }

    fun getCharacteristicsList(
        uid: Int,
        aid: Int,
    ): GetCharacteristicsListResult {
        return transactionManager.run {
            val characteristicsRepository = it.characteristicsRepository
            val athleteRepository = it.athleteRepository

            val athlete = athleteRepository.getAthlete(aid)
                ?: return@run failure(GetCharacteristicsListError.AthleteNotFound)

            if (uid != aid && uid != athlete.coach) return@run failure(GetCharacteristicsListError.NotAthletesCoach)

            val characteristicsList = characteristicsRepository.getCharacteristicsList(aid)
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
        thighFat: Int?,
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
                thighFat,
            )
        ) {
            return failure(UpdateCharacteristicsError.InvalidCharacteristics)
        }

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
                thighFat,
            )
            success(uid)
        }
    }

    fun removeCharacteristics(
        coachId: Int,
        uid: Int,
        date: String,
    ): RemoveCharacteristicsResult {
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

    fun setCalendar(
        cid: Int,
        uid: Int,
        calendar: List<MesocycleInputInfo>,
    ): SetCalendarResult {
        return transactionManager.run {
            val athleteRepository = it.athleteRepository
            val activityRepository = it.activityRepository

            val athlete = athleteRepository.getAthlete(uid) ?: return@run failure(SetCalendarError.AthleteNotFound)
            if (athlete.coach != cid) return@run failure(SetCalendarError.NotAthletesCoach)

            for (meso in calendar) {
                if (!activityDomain.areDatesValid(meso.startTime, meso.endTime))
                    return@run failure(SetCalendarError.InvalidMesocycle)

                val mesoId: Int
                if (meso.id != null) {
                    val mesoDb = activityRepository.getMesocycle(meso.id)
                        ?: return@run failure(SetCalendarError.MesocycleNotFound)

                    if (activityDomain.compareCycles(mesoDb.startTime, mesoDb.endTime, meso.startTime, meso.endTime))
                        activityRepository.updateMesocycle(meso.id, meso.startTime, meso.endTime)

                    mesoId = meso.id
                }
                else mesoId = activityRepository.storeMesocycle(uid, meso.startTime, meso.endTime)

                for (micro in meso.microcycles) {
                    if (
                        !activityDomain.areDatesValid(micro.startTime, micro.endTime)
                        || !activityDomain.areDatesValid(meso.startTime, micro.startTime)
                        || !activityDomain.areDatesValid(micro.endTime, meso.endTime)
                    )
                        return@run failure(SetCalendarError.InvalidMicrocycle)

                    if (micro.id != null) {
                        val microDb = activityRepository.getMicrocycle(micro.id)
                            ?: return@run failure(SetCalendarError.MicrocycleNotFound)

                        if (activityDomain.compareCycles(microDb.startTime, microDb.endTime, micro.startTime, micro.endTime))
                            activityRepository.updateMicrocycle(micro.id, micro.startTime, micro.endTime)
                    }
                    else activityRepository.storeMicrocycle(mesoId, micro.startTime, micro.endTime)
                }
            }
            success(true)
        }
    }

    fun getCalendar(
        uid: Int,
        aid: Int,
        type: String?,
    ) : GetCalendarResult {
        val activityType = activityDomain.isValidType(type ?: "")

        return transactionManager.run {
            val athleteRepository = it.athleteRepository
            val activityRepository = it.activityRepository

            val athlete = athleteRepository.getAthlete(aid) ?: return@run failure(GetCalendarError.AthleteNotFound)
            if (uid != aid && athlete.coach != uid) return@run failure(GetCalendarError.NotAthletesCoach)

            success(activityRepository.getCalendar(aid, activityType))
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

package waveCoach.services

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import waveCoach.domain.*
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

data class HeatInputInfo(
    val score: Int,
    val waterActivity: WaterActivityInputInfo,
)

data class WaterActivityInputInfo(
    val athleteId: Int,
    val rpe: Int,
    val condition: String,
    val trimp: Int,
    val duration: Int,
    val waves: List<WaveInputInfo>,
)

data class UpdateHeatInputInfo(
    val id: Int?,
    val score: Int?,
    val waterActivity: UpdateWaterActivityInputInfo?,
)

data class UpdateWaterActivityInputInfo(
    val id: Int?,
    val rpe: Int?,
    val condition: String?,
    val trimp: Int?,
    val duration: Int?,
    val waves: List<UpdateWaveInputInfo>?,
)

sealed class CreateAthleteError {
    data object InvalidBirthdate : CreateAthleteError()

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
    data object Invalidbirthdate : UpdateAthleteError()

    data object InvalidName : UpdateAthleteError()

    data object AthleteNotFound : UpdateAthleteError()

    data object NotAthletesCoach : UpdateAthleteError()

    data object InvalidPhoto : UpdateAthleteError()
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

sealed class GetCalendarError {
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

sealed class GetWaterActivitiesError {
    data object AthleteNotFound : GetWaterActivitiesError()

    data object NotAthletesCoach : GetWaterActivitiesError()
}
typealias GetWaterActivitiesResult = Either<GetWaterActivitiesError, List<MesocycleWater>>

sealed class CreateCompetitionError {
    data object InvalidDate : CreateCompetitionError()

    data object AthleteNotFound : CreateCompetitionError()

    data object NotAthletesCoach : CreateCompetitionError()

    data object ActivityWithoutMicrocycle : CreateCompetitionError()

    data object InvalidRpe : CreateCompetitionError()

    data object InvalidTrimp : CreateCompetitionError()

    data object InvalidDuration : CreateCompetitionError()

    data object InvalidWaterManeuver : CreateCompetitionError()

    data object InvalidScore : CreateCompetitionError()

    data object InvalidPlace : CreateCompetitionError()

    data object InvalidName : CreateCompetitionError()
}
typealias CreateCompetitionResult = Either<CreateCompetitionError, Int>

sealed class GetCompetitionError {
    data object CompetitionNotFound : GetCompetitionError()

    data object AthleteNotFound : GetCompetitionError()

    data object NotAthletesCoach : GetCompetitionError()

    data object NotAthletesCompetition : GetCompetitionError()
}
typealias GetCompetitionResult = Either<GetCompetitionError, CompetitionWithHeats>

sealed class GetCompetitionsError {
    data object AthleteNotFound : GetCompetitionsError()

    data object NotAthletesCoach : GetCompetitionsError()
}
typealias GetCompetitionsResult = Either<GetCompetitionsError, List<CompetitionWithHeats>>

sealed class UpdateCompetitionError {
    data object CompetitionNotFound : UpdateCompetitionError()

    data object AthleteNotFound : UpdateCompetitionError()

    data object NotAthletesCoach : UpdateCompetitionError()

    data object NotAthletesCompetition : UpdateCompetitionError()

    data object InvalidScore : UpdateCompetitionError()

    data object InvalidDate : UpdateCompetitionError()

    data object InvalidPlace : UpdateCompetitionError()

    data object HeatNotFound : UpdateCompetitionError()

    data object InvalidWaterManeuver : UpdateCompetitionError()

    data object InvalidRpe : UpdateCompetitionError()

    data object InvalidTrimp : UpdateCompetitionError()

    data object InvalidDuration : UpdateCompetitionError()

    data object InvalidWaveOrder : UpdateCompetitionError()

    data object InvalidManeuverOrder : UpdateCompetitionError()

    data object WaveNotFound : UpdateCompetitionError()

    data object ManeuverNotFound : UpdateCompetitionError()

    data object InvalidSuccess : UpdateCompetitionError()

    data object InvalidRightSide : UpdateCompetitionError()

    data object InvalidManeuvers : UpdateCompetitionError()

    data object InvalidWaterActivity : UpdateCompetitionError()

    data object InvalidWaves : UpdateCompetitionError()

    data object InvalidCondition : UpdateCompetitionError()

    data object ActivityWithoutMicrocycle : UpdateCompetitionError()

    data object InvalidName : UpdateCompetitionError()
}
typealias UpdateCompetitionResult = Either<UpdateCompetitionError, Int>

sealed class RemoveCompetitionError {
    data object AthleteNotFound : RemoveCompetitionError()

    data object CompetitionNotFound : RemoveCompetitionError()

    data object NotAthletesCoach : RemoveCompetitionError()

    data object NotAthletesCompetition : RemoveCompetitionError()
}
typealias RemoveCompetitionResult = Either<RemoveCompetitionError, Int>

@Component
class AthleteServices(
    private val transactionManager: TransactionManager,
    private val athleteDomain: AthleteDomain,
    private val characteristicsDomain: CharacteristicsDomain,
    private val userDomain: UserDomain,
    private val activityDomain: ActivityDomain,
    private val waterActivityDomain: WaterActivityDomain,
    private val cloudinaryServices: CloudinaryServices,
    private val clock: Clock,
) {
    fun createAthlete(
        name: String,
        coachId: Int,
        birthdate: String,
        photo: MultipartFile?,
    ): CreateAthleteResult {
        val username = athleteDomain.createAthleteUsername()
        val passwordValidationInfo =
            userDomain.createPasswordValidationInformation(athleteDomain.athleteDefaultPassword)

        if (!athleteDomain.isNameValid(name)) return failure(CreateAthleteError.InvalidName)
        val date = dateToLongWithVerification(birthdate) ?: return failure(CreateAthleteError.InvalidBirthdate)

        val url =
            photo?.let {
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
        name: String?,
        birthdate: String?,
        photo: MultipartFile?,
    ): UpdateAthleteResult {
        val date =
            birthdate?.let {
                dateToLongWithVerification(birthdate) ?: return failure(UpdateAthleteError.Invalidbirthdate)
            }

        if (name != null && !athleteDomain.isNameValid(name)) return failure(UpdateAthleteError.InvalidName)

        return transactionManager.run {
            val athleteRepository = it.athleteRepository

            val athlete = athleteRepository.getAthlete(aid) ?: return@run failure(UpdateAthleteError.AthleteNotFound)
            if (athlete.coach != coachId) return@run failure(UpdateAthleteError.NotAthletesCoach)

            val url =
                photo?.let { photo ->
                    cloudinaryServices.uploadAthleteImage(photo)
                        ?: return@run failure(UpdateAthleteError.InvalidPhoto)
                }

            athleteRepository.updateAthlete(aid, name, date, url)

            if (url != null && athlete.url != null) {
                cloudinaryServices.deleteImage(athlete.url!!)
            }

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
            val athlete = athleteRepository.getAthlete(aid) ?: return@run failure(RemoveAthleteError.AthleteNotFound)
            if (athlete.coach != coachId) return@run failure(RemoveAthleteError.NotAthletesCoach)

            userRepository.removeUser(aid)

            athlete.url?.let { url -> cloudinaryServices.deleteImage(url) }

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
            date?.let { dateToLongWithVerification(it) }
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
        val dateLong = dateToLongWithVerification(date) ?: return failure(GetCharacteristicsError.InvalidDate)

        return transactionManager.run {
            val characteristicsRepository = it.characteristicsRepository
            val athleteRepository = it.athleteRepository

            val athlete =
                athleteRepository.getAthlete(aid)
                    ?: return@run failure(GetCharacteristicsError.AthleteNotFound)

            if (uid != aid && uid != athlete.coach) return@run failure(GetCharacteristicsError.NotAthletesCoach)

            val characteristics =
                characteristicsRepository.getCharacteristics(aid, dateLong)
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

            val athlete =
                athleteRepository.getAthlete(aid)
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
        val dateLong = dateToLongWithVerification(date) ?: return failure(UpdateCharacteristicsError.InvalidDate)

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
        val dateLong = dateToLongWithVerification(date) ?: return failure(RemoveCharacteristicsError.InvalidDate)

        return transactionManager.run {
            val characteristicsRepository = it.characteristicsRepository
            val athleteRepository = it.athleteRepository

            val athlete =
                athleteRepository.getAthlete(uid)
                    ?: return@run failure(RemoveCharacteristicsError.AthleteNotFound)

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
                if (!activityDomain.areDatesValid(meso.startTime, meso.endTime)) {
                    return@run failure(SetCalendarError.InvalidMesocycle)
                }

                val mesoId: Int
                if (meso.id != null) {
                    val mesoDb =
                        activityRepository.getMesocycle(meso.id)
                            ?: return@run failure(SetCalendarError.MesocycleNotFound)

                    if (!activityDomain.compareCycles(mesoDb.startTime, mesoDb.endTime, meso.startTime, meso.endTime)) {
                        activityRepository.updateMesocycle(meso.id, meso.startTime, meso.endTime)
                    }

                    mesoId = meso.id
                } else {
                    mesoId = activityRepository.storeMesocycle(uid, meso.startTime, meso.endTime)
                }

                for (micro in meso.microcycles) {
                    if (
                        !activityDomain.areDatesValid(micro.startTime, micro.endTime) ||
                        !activityDomain.areDatesValid(meso.startTime, micro.startTime) ||
                        !activityDomain.areDatesValid(micro.endTime, meso.endTime)
                    ) {
                        return@run failure(SetCalendarError.InvalidMicrocycle)
                    }

                    if (micro.id != null) {
                        val microDb =
                            activityRepository.getMicrocycle(micro.id)
                                ?: return@run failure(SetCalendarError.MicrocycleNotFound)

                        if (!activityDomain.compareCycles(
                                microDb.startTime,
                                microDb.endTime,
                                micro.startTime,
                                micro.endTime,
                            )
                        ) {
                            activityRepository.updateMicrocycle(micro.id, micro.startTime, micro.endTime)
                        }
                    } else {
                        activityRepository.storeMicrocycle(mesoId, micro.startTime, micro.endTime)
                    }
                }
            }
            success(true)
        }
    }

    fun getCalendar(
        uid: Int,
        aid: Int,
        type: String?,
    ): GetCalendarResult {
        val activityType = activityDomain.isValidType(type ?: "")

        return transactionManager.run {
            val athleteRepository = it.athleteRepository
            val activityRepository = it.activityRepository

            val athlete = athleteRepository.getAthlete(aid) ?: return@run failure(GetCalendarError.AthleteNotFound)
            if (uid != aid && athlete.coach != uid) return@run failure(GetCalendarError.NotAthletesCoach)

            success(activityRepository.getCalendar(aid, activityType))
        }
    }

    fun getWaterActivities(
        uid: Int,
        aid: Int,
    ): GetWaterActivitiesResult {
        return transactionManager.run {
            val athleteRepository = it.athleteRepository
            val waterActivityRepository = it.waterActivityRepository

            val athlete =
                athleteRepository.getAthlete(aid) ?: return@run failure(GetWaterActivitiesError.AthleteNotFound)
            if (uid != aid && athlete.coach != uid) return@run failure(GetWaterActivitiesError.NotAthletesCoach)

            success(waterActivityRepository.getWaterActivities(aid))
        }
    }

    fun createCompetition(
        coachId: Int,
        athleteId: Int,
        date: String,
        location: String,
        place: Int,
        name: String,
        heats: List<HeatInputInfo>,
    ): CreateCompetitionResult {
        val dateLong = dateToLong(date) ?: return failure(CreateCompetitionError.InvalidDate)

        return transactionManager.run {
            val athleteRepository = it.athleteRepository
            val competitionRepository = it.competitionRepository
            val waterActivityRepository = it.waterActivityRepository
            val waterManeuverRepository = it.waterManeuverRepository
            val activityRepository = it.activityRepository

            val athlete =
                athleteRepository.getAthlete(athleteId)
                    ?: return@run failure(CreateCompetitionError.AthleteNotFound)

            if (athlete.coach != coachId) return@run failure(CreateCompetitionError.NotAthletesCoach)

            if (place <= 0) return@run failure(CreateCompetitionError.InvalidPlace)

            if (name.isBlank()) return@run failure(CreateCompetitionError.InvalidName)

            val competitionId = competitionRepository.storeCompetition(athleteId, dateLong, location, place, name)

            val heatsToInsert =
                heats.map { heat ->
                    if (heat.score < 0) {
                        return@run failure(CreateCompetitionError.InvalidScore)
                    }

                    val micro =
                        activityRepository.getMicrocycleByDate(dateLong, athleteId)
                            ?: return@run failure(CreateCompetitionError.ActivityWithoutMicrocycle)

                    val activityId = activityRepository.storeActivity(athleteId, dateLong, micro.id)

                    if (!waterActivityDomain.checkRpe(heat.waterActivity.rpe)) {
                        return@run failure(CreateCompetitionError.InvalidRpe)
                    }

                    if (!waterActivityDomain.checkTrimp(heat.waterActivity.trimp)) {
                        return@run failure(CreateCompetitionError.InvalidTrimp)
                    }

                    if (!waterActivityDomain.checkDuration(heat.waterActivity.duration)) {
                        return@run failure(CreateCompetitionError.InvalidDuration)
                    }

                    val waterActivityId =
                        waterActivityRepository.storeWaterActivity(
                            activityId,
                            heat.waterActivity.rpe,
                            heat.waterActivity.condition,
                            heat.waterActivity.trimp,
                            heat.waterActivity.duration,
                        )

                    val wavesToInsert =
                        heat.waterActivity.waves.mapIndexed { order, wave ->
                            WaveToInsert(
                                waterActivityId,
                                wave.points,
                                wave.rightSide,
                                order,
                            )
                        }

                    val wavesIds = waterActivityRepository.storeWaves(wavesToInsert)

                    val maneuversToInsert =
                        heat.waterActivity.waves.flatMapIndexed { wavesIndex, wave ->
                            wave.maneuvers.mapIndexed { maneuverOrder, maneuver ->
                                if (waterManeuverRepository.getWaterManeuverById(maneuver.waterManeuverId) == null) {
                                    return@run failure(CreateCompetitionError.InvalidWaterManeuver)
                                }

                                ManeuverToInsert(
                                    wavesIds[wavesIndex],
                                    maneuver.waterManeuverId,
                                    maneuver.success,
                                    maneuverOrder,
                                )
                            }
                        }

                    waterActivityRepository.storeManeuvers(maneuversToInsert)

                    HeatToInsert(
                        competitionId,
                        waterActivityId,
                        heat.score,
                    )
                }

            competitionRepository.storeHeats(heatsToInsert)
            success(competitionId)
        }
    }

    fun getCompetition(
        uid: Int,
        athleteId: Int,
        competitionId: Int,
    ): GetCompetitionResult {
        return transactionManager.run {
            val athleteRepository = it.athleteRepository
            val competitionRepository = it.competitionRepository

            val competition =
                competitionRepository.getCompetition(competitionId)
                    ?: return@run failure(GetCompetitionError.CompetitionNotFound)

            val athlete =
                athleteRepository.getAthlete(athleteId)
                    ?: return@run failure(GetCompetitionError.AthleteNotFound)

            if (uid != athleteId && athlete.coach != uid) return@run failure(GetCompetitionError.NotAthletesCoach)

            if (competition.uid != athleteId) return@run failure(GetCompetitionError.NotAthletesCompetition)

            success(competition)
        }
    }

    fun getCompetitions(
        uid: Int,
        athleteId: Int,
    ): GetCompetitionsResult {
        return transactionManager.run {
            val athleteRepository = it.athleteRepository
            val competitionRepository = it.competitionRepository

            val athlete =
                athleteRepository.getAthlete(athleteId)
                    ?: return@run failure(GetCompetitionsError.AthleteNotFound)

            if (uid != athleteId && athlete.coach != uid) return@run failure(GetCompetitionsError.NotAthletesCoach)

            val competitions = competitionRepository.getCompetitionsByAthlete(athleteId)
            success(competitions)
        }
    }

    fun updateCompetition(
        coachId: Int,
        athleteId: Int,
        competitionId: Int,
        date: String?,
        location: String?,
        place: Int?,
        name: String?,
        heats: List<UpdateHeatInputInfo>?,
    ): UpdateCompetitionResult {
        val dateLong = date?.let { dateToLong(it) ?: return failure(UpdateCompetitionError.InvalidDate) }

        return transactionManager.run { it ->
            val athleteRepository = it.athleteRepository
            val competitionRepository = it.competitionRepository
            val activityRepository = it.activityRepository
            val waterActivityRepository = it.waterActivityRepository
            val waterManeuverRepository = it.waterManeuverRepository

            val competition =
                competitionRepository.getCompetition(competitionId)
                    ?: return@run failure(UpdateCompetitionError.CompetitionNotFound)

            val athlete =
                athleteRepository.getAthlete(athleteId)
                    ?: return@run failure(UpdateCompetitionError.AthleteNotFound)

            if (athlete.coach != coachId) return@run failure(UpdateCompetitionError.NotAthletesCoach)

            if (competition.uid != athleteId) return@run failure(UpdateCompetitionError.NotAthletesCompetition)

            if (date != null || location != null || place != null) {
                if (place != null && place <= 0) return@run failure(UpdateCompetitionError.InvalidPlace)

                if (name != null && name.isBlank()) return@run failure(UpdateCompetitionError.InvalidName)

                competitionRepository.updateCompetition(competitionId, dateLong, location, place, name)
            }

            if (heats != null) {
                val (create, update, delete) = separateCreateUpdateDelete(heats)

                val heatsOnDB = competitionRepository.getHeatsByCompetition(competitionId)

                // Update existing heats
                val heatsToUpdate =
                    update.map { heat ->
                        if (heat.score != null && heat.score < 0) {
                            return@run failure(UpdateCompetitionError.InvalidScore)
                        }

                        if (heatsOnDB.none { it.id == heat.id }) return@run failure(UpdateCompetitionError.HeatNotFound)

                        val activity = activityRepository.getActivityByHeatId(heat.id!!)

                        if (dateLong != null && dateLong != activity!!.date) {
                            activityRepository.updateActivity(activity.id, dateLong)
                        }

                        val waterActivityId = heat.waterActivity?.id

                        if (waterActivityId != null) {
                            if (
                                heat.waterActivity.rpe != null ||
                                heat.waterActivity.condition != null ||
                                heat.waterActivity.trimp != null ||
                                heat.waterActivity.duration != null
                            ) {
                                if (heat.waterActivity.rpe != null && !waterActivityDomain.checkRpe(heat.waterActivity.rpe)) {
                                    return@run failure(UpdateCompetitionError.InvalidRpe)
                                }

                                if (heat.waterActivity.trimp != null && !waterActivityDomain.checkTrimp(heat.waterActivity.trimp)) {
                                    return@run failure(UpdateCompetitionError.InvalidTrimp)
                                }

                                if (heat.waterActivity.duration != null && !waterActivityDomain.checkDuration(heat.waterActivity.duration)) {
                                    return@run failure(UpdateCompetitionError.InvalidDuration)
                                }

                                waterActivityRepository.updateWaterActivity(
                                    waterActivityId,
                                    heat.waterActivity.rpe,
                                    heat.waterActivity.condition,
                                    heat.waterActivity.trimp,
                                    heat.waterActivity.duration,
                                )
                            }

                            if (heat.waterActivity.waves != null) {
                                val (createWaves, updateWaves, deleteWaves) =
                                    separateCreateUpdateDelete(heat.waterActivity.waves)

                                val wavesOnDB = waterActivityRepository.getWavesByActivity(waterActivityId)

                                // Update existing waves
                                val wavesToUpdate =
                                    updateWaves.map { wave ->
                                        if (wave.order != null &&
                                            (
                                                wave.order <= 0 ||
                                                    !checkOrderConflict(wavesOnDB, heat.waterActivity.waves, "waveOrder", wave.order)
                                            )
                                        ) {
                                            return@run failure(UpdateCompetitionError.InvalidWaveOrder)
                                        }

                                        if (wavesOnDB.none { it.id == wave.id }) {
                                            return@run failure(UpdateCompetitionError.WaveNotFound)
                                        }

                                        WaveToUpdate(
                                            wave.id!!,
                                            wave.points,
                                            wave.rightSide,
                                            wave.order,
                                        )
                                    }
                                val maneuversCreate = mutableListOf<ManeuverToInsert>()
                                val maneuversUpdate = mutableListOf<ManeuverToUpdate>()
                                val maneuversDelete = mutableListOf<Int>()

                                updateWaves.forEachIndexed { waveIndex, wave ->
                                    if (wave.maneuvers != null) {
                                        val (maneuverCreate, maneuverUpdate, maneuverDelete) =
                                            separateCreateUpdateDelete(wave.maneuvers)

                                        val maneuversOnDB =
                                            waterActivityRepository.getManeuversByWave(update[waveIndex].id!!)

                                        // Update existing Maneuvers
                                        maneuverUpdate.forEach {
                                            if (it.waterManeuverId != null && it.waterManeuverId <= 0 &&
                                                waterManeuverRepository.getWaterManeuverById(it.waterManeuverId) != null
                                            ) {
                                                return@run failure(UpdateCompetitionError.InvalidWaterManeuver)
                                            }

                                            if (maneuversOnDB.none { maneuver -> maneuver.id == it.id }) {
                                                return@run failure(UpdateCompetitionError.ManeuverNotFound)
                                            }

                                            if (it.order != null && (
                                                    it.order <= 0 ||
                                                        !checkOrderConflict(
                                                            maneuversOnDB, wave.maneuvers, "maneuverOrder", it.order,
                                                        )
                                                )
                                            ) {
                                                return@run failure(UpdateCompetitionError.InvalidManeuverOrder)
                                            }

                                            maneuversUpdate.add(
                                                ManeuverToUpdate(
                                                    it.id!!,
                                                    it.waterManeuverId,
                                                    it.success,
                                                    it.order,
                                                ),
                                            )
                                        }
                                        waterActivityRepository.updateManeuvers(maneuversUpdate)

                                        // Add New Maneuvers
                                        maneuverCreate.forEach { maneuver ->
                                            if (maneuver.waterManeuverId == null || maneuver.waterManeuverId <= 0 ||
                                                waterManeuverRepository.getWaterManeuverById(maneuver.waterManeuverId) == null
                                            ) {
                                                return@run failure(UpdateCompetitionError.InvalidWaterManeuver)
                                            }

                                            if (maneuver.success == null) {
                                                return@run failure(UpdateCompetitionError.InvalidSuccess)
                                            }

                                            if (maneuver.order == null || maneuver.order <= 0) {
                                                return@run failure(UpdateCompetitionError.InvalidManeuverOrder)
                                            }

                                            maneuversCreate.add(
                                                ManeuverToInsert(
                                                    update[waveIndex].id!!,
                                                    maneuver.waterManeuverId,
                                                    maneuver.success,
                                                    maneuver.order,
                                                ),
                                            )
                                        }

                                        // Delete Maneuvers
                                        if (maneuverDelete.any { id -> maneuversOnDB.none { it.id == id } }) {
                                            return@run failure(UpdateCompetitionError.ManeuverNotFound)
                                        }

                                        maneuversDelete.addAll(maneuverDelete)
                                    }
                                    waterActivityRepository.updateWaves(wavesToUpdate)

                                    waterActivityRepository.removeManeuversById(maneuversDelete)
                                }

                                // Add New waves
                                val wavesToInsert =
                                    createWaves.map { wave ->
                                        if (wave.order == null || wave.order <= 0 ||
                                            waterActivityRepository.verifyWaveOrder(waterActivityId, wave.order)
                                        ) {
                                            return@run failure(UpdateCompetitionError.InvalidWaveOrder)
                                        }

                                        if (wave.rightSide == null) return@run failure(UpdateCompetitionError.InvalidRightSide)

                                        WaveToInsert(
                                            waterActivityId,
                                            wave.points,
                                            wave.rightSide,
                                            wave.order,
                                        )
                                    }

                                val wavesCreatedIds = waterActivityRepository.storeWaves(wavesToInsert)

                                val maneuversToInsert =
                                    createWaves.flatMapIndexed { wavesIndex, wave ->
                                        if (wave.maneuvers == null) return@run failure(UpdateCompetitionError.InvalidManeuvers)

                                        wave.maneuvers.mapIndexed { maneuverIndex, maneuver ->
                                            if (maneuver.waterManeuverId == null || maneuver.waterManeuverId <= 0) {
                                                return@run failure(UpdateCompetitionError.InvalidWaterManeuver)
                                            }

                                            if (maneuver.success == null) {
                                                return@run failure(UpdateCompetitionError.InvalidSuccess)
                                            }

                                            ManeuverToInsert(
                                                wavesCreatedIds[wavesIndex],
                                                maneuver.waterManeuverId,
                                                maneuver.success,
                                                maneuverIndex + 1,
                                            )
                                        }
                                    }
                                waterActivityRepository.storeManeuvers(maneuversToInsert + maneuversCreate)

                                // Delete waves
                                if (deleteWaves.any { id -> wavesOnDB.none { it.id == id } }) {
                                    return@run failure(UpdateCompetitionError.WaveNotFound)
                                }
                                waterActivityRepository.removeWavesById(delete)
                            }
                        }

                        HeatToUpdate(
                            heat.id,
                            heat.score,
                        )
                    }
                competitionRepository.updateHeats(heatsToUpdate)

                // Add New heats
                val heatsToInsert =
                    create.map { heat ->
                        if (heat.waterActivity == null) return@run failure(UpdateCompetitionError.InvalidWaterActivity)

                        if (heat.score == null || heat.score < 0) return@run failure(UpdateCompetitionError.InvalidScore)

                        if (dateLong == null) return@run failure(UpdateCompetitionError.InvalidDate)

                        val micro =
                            activityRepository.getMicrocycleByDate(dateLong, athleteId)
                                ?: return@run failure(UpdateCompetitionError.ActivityWithoutMicrocycle)

                        val activityId = activityRepository.storeActivity(athleteId, dateLong, micro.id)

                        if (heat.waterActivity.rpe == null || !waterActivityDomain.checkRpe(heat.waterActivity.rpe)) {
                            return@run failure(UpdateCompetitionError.InvalidRpe)
                        }

                        if (heat.waterActivity.trimp == null || !waterActivityDomain.checkTrimp(heat.waterActivity.trimp)) {
                            return@run failure(UpdateCompetitionError.InvalidTrimp)
                        }

                        if (heat.waterActivity.duration == null || !waterActivityDomain.checkDuration(heat.waterActivity.duration)) {
                            return@run failure(UpdateCompetitionError.InvalidDuration)
                        }

                        if (heat.waterActivity.condition == null) return@run failure(UpdateCompetitionError.InvalidCondition)

                        val waterActivityId =
                            waterActivityRepository.storeWaterActivity(
                                activityId,
                                heat.waterActivity.rpe,
                                heat.waterActivity.condition,
                                heat.waterActivity.trimp,
                                heat.waterActivity.duration,
                            )

                        if (heat.waterActivity.waves == null) {
                            return@run failure(UpdateCompetitionError.InvalidWaves)
                        }

                        val wavesToInsert =
                            heat.waterActivity.waves.mapIndexed { order, wave ->

                                if (wave.rightSide == null) {
                                    return@run failure(UpdateCompetitionError.InvalidRightSide)
                                }

                                WaveToInsert(
                                    waterActivityId,
                                    wave.points,
                                    wave.rightSide,
                                    order,
                                )
                            }

                        val wavesIds = waterActivityRepository.storeWaves(wavesToInsert)

                        val maneuversToInsert =
                            heat.waterActivity.waves.flatMapIndexed { wavesIndex, wave ->
                                if (wave.maneuvers == null) return@run failure(UpdateCompetitionError.InvalidManeuvers)

                                wave.maneuvers.mapIndexed { maneuverOrder, maneuver ->
                                    if (maneuver.success == null) return@run failure(UpdateCompetitionError.InvalidSuccess)

                                    if (maneuver.waterManeuverId == null ||
                                        waterManeuverRepository.getWaterManeuverById(maneuver.waterManeuverId) == null
                                    ) {
                                        return@run failure(UpdateCompetitionError.InvalidWaterManeuver)
                                    }

                                    ManeuverToInsert(
                                        wavesIds[wavesIndex],
                                        maneuver.waterManeuverId,
                                        maneuver.success,
                                        maneuverOrder,
                                    )
                                }
                            }

                        waterActivityRepository.storeManeuvers(maneuversToInsert)

                        HeatToInsert(
                            competitionId,
                            waterActivityId,
                            heat.score,
                        )
                    }
                competitionRepository.storeHeats(heatsToInsert)

                // Delete heats
                if (delete.any { id -> heatsOnDB.none { it.id == id } }) {
                    return@run failure(UpdateCompetitionError.HeatNotFound)
                }
                competitionRepository.removeHeatsById(delete)
            }

            success(competitionId)
        }
    }

    fun removeCompetition(
        coachId: Int,
        athleteId: Int,
        competitionId: Int,
    ): RemoveCompetitionResult {
        return transactionManager.run {
            val athleteRepository = it.athleteRepository
            val competitionRepository = it.competitionRepository

            val competition =
                competitionRepository.getCompetition(competitionId)
                    ?: return@run failure(RemoveCompetitionError.CompetitionNotFound)

            val athlete =
                athleteRepository.getAthlete(athleteId)
                    ?: return@run failure(RemoveCompetitionError.AthleteNotFound)

            if (athlete.coach != coachId) return@run failure(RemoveCompetitionError.NotAthletesCoach)

            if (competition.uid != athleteId) return@run failure(RemoveCompetitionError.NotAthletesCompetition)

            competitionRepository.removeCompetition(competitionId)
            success(competitionId)
        }
    }

    private fun dateToLongWithVerification(date: String): Long? {
        return try {
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            val dateParsed = LocalDate.parse(date, formatter)

            if (!dateParsed.isAfter(LocalDate.now())) {
                dateParsed.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            } else {
                null
            }
        } catch (e: DateTimeParseException) {
            null
        }
    }
}

package waveCoach.services

import org.springframework.stereotype.Component
import waveCoach.domain.ActivityType
import waveCoach.domain.ManeuverToInsert
import waveCoach.domain.Questionnaire
import waveCoach.domain.WaterActivityDomain
import waveCoach.domain.WaterActivityWithWaves
import waveCoach.domain.WaveToInsert
import waveCoach.repository.TransactionManager
import waveCoach.utils.Either
import waveCoach.utils.failure
import waveCoach.utils.success

data class WaveInputInfo(
    val points: Float?,
    val rightSide: Boolean,
    val maneuvers: List<ManeuverInputInfo>,
)

data class ManeuverInputInfo(
    val waterManeuverId: Int,
    val success: Boolean,
)

sealed class CreateWaterActivityError {
    data object InvalidDate : CreateWaterActivityError()

    data object AthleteNotFound : CreateWaterActivityError()

    data object NotAthletesCoach : CreateWaterActivityError()

    data object ActivityWithoutMicrocycle : CreateWaterActivityError()

    data object InvalidRpe : CreateWaterActivityError()

    data object InvalidTrimp : CreateWaterActivityError()

    data object InvalidDuration : CreateWaterActivityError()

    data object InvalidWaterManeuver : CreateWaterActivityError()
}
typealias CreateWaterActivityResult = Either<CreateWaterActivityError, Int>

sealed class GetWaterActivityError {
    data object ActivityNotFound : GetWaterActivityError()

    data object NotAthletesCoach : GetWaterActivityError()

    data object NotWaterActivity : GetWaterActivityError()
}
typealias GetWaterActivityResult = Either<GetWaterActivityError, WaterActivityWithWaves>

sealed class RemoveWaterActivityError {
    data object ActivityNotFound : RemoveWaterActivityError()

    data object NotAthletesCoach : RemoveWaterActivityError()

    data object NotWaterActivity : RemoveWaterActivityError()
}
typealias RemoveWaterActivityResult = Either<RemoveWaterActivityError, Int>

sealed class CreateQuestionnaireError {
    data object AlreadyExists : CreateQuestionnaireError()

    data object ActivityNotFound : CreateQuestionnaireError()

    data object NotAthletesCoach : CreateQuestionnaireError()

    data object InvalidSleep : CreateQuestionnaireError()

    data object InvalidFatigue : CreateQuestionnaireError()

    data object InvalidStress : CreateQuestionnaireError()

    data object InvalidMusclePain : CreateQuestionnaireError()
}
typealias CreateQuestionnaireResult = Either<CreateQuestionnaireError, Unit>

sealed class GetQuestionnaireError {
    data object ActivityNotFound : GetQuestionnaireError()

    data object QuestionnaireNotFound : GetQuestionnaireError()

    data object NotAthletesCoach : GetQuestionnaireError()
}
typealias GetQuestionnaireResult = Either<GetQuestionnaireError, Questionnaire>

@Component
class WaterActivityServices(
    private val transactionManager: TransactionManager,
    private val waterActivityDomain: WaterActivityDomain,
) {
    fun createWaterActivity(
        coachId: Int,
        athleteId: Int,
        date: String,
        pse: Int,
        condition: String,
        trimp: Int,
        duration: Int,
        waves: List<WaveInputInfo>,
    ): CreateWaterActivityResult {
        val dateLong = dateToLong(date) ?: return failure(CreateWaterActivityError.InvalidDate)

        return transactionManager.run {
            val athleteRepository = it.athleteRepository
            val activityRepository = it.activityRepository
            val waterActivityRepository = it.waterActivityRepository
            val waterManeuverRepository = it.waterManeuverRepository

            val athlete =
                athleteRepository.getAthlete(athleteId)
                    ?: return@run failure(CreateWaterActivityError.AthleteNotFound)

            if (athlete.coach != coachId) {
                return@run failure(CreateWaterActivityError.NotAthletesCoach)
            }

            val micro =
                activityRepository.getMicrocycleByDate(dateLong, athleteId)
                    ?: return@run failure(CreateWaterActivityError.ActivityWithoutMicrocycle)

            val activityId = activityRepository.storeActivity(athleteId, dateLong, micro.id)

            if (!waterActivityDomain.checkRpe(pse)) {
                return@run failure(CreateWaterActivityError.InvalidRpe)
            }

            if (!waterActivityDomain.checkTrimp(trimp)) {
                return@run failure(CreateWaterActivityError.InvalidTrimp)
            }

            if (!waterActivityDomain.checkDuration(duration)) {
                return@run failure(CreateWaterActivityError.InvalidDuration)
            }

            val waterActivityId =
                waterActivityRepository.storeWaterActivity(
                    activityId,
                    pse,
                    condition,
                    trimp,
                    duration,
                )

            val wavesToInsert =
                waves.mapIndexed { order, wave ->
                    WaveToInsert(
                        waterActivityId,
                        wave.points,
                        wave.rightSide,
                        order,
                    )
                }

            val wavesIds = waterActivityRepository.storeWaves(wavesToInsert)

            val maneuversToInsert = waves.flatMapIndexed { wavesIndex, wave ->
                wave.maneuvers.mapIndexed { maneuverOrder, maneuver ->
                    if (waterManeuverRepository.getWaterManeuverById(maneuver.waterManeuverId) == null)
                        return@run failure(CreateWaterActivityError.InvalidWaterManeuver)

                    ManeuverToInsert(
                        wavesIds[wavesIndex],
                        maneuver.waterManeuverId,
                        maneuver.success,
                        maneuverOrder,
                    )
                }
            }

            waterActivityRepository.storeManeuvers(maneuversToInsert)

            success(waterActivityId)
        }
    }

    fun getWaterActivity(
        uid: Int,
        activityId: Int,
    ): GetWaterActivityResult {
        return transactionManager.run {
            val athleteRepository = it.athleteRepository
            val activityRepository = it.activityRepository
            val waterActivityRepository = it.waterActivityRepository

            val activity =
                activityRepository.getActivityById(activityId)
                    ?: return@run failure(GetWaterActivityError.ActivityNotFound)

            if (activity.type != ActivityType.WATER) {
                return@run failure(GetWaterActivityError.NotWaterActivity)
            }

            val athlete = athleteRepository.getAthlete(activity.uid)!!

            if (athlete.uid != uid && athlete.coach != uid) {
                return@run failure(GetWaterActivityError.NotAthletesCoach)
            }

            val waterActivity = waterActivityRepository.getWaterActivity(activityId)!!

            success(waterActivity)
        }
    }

    fun removeWaterActivity(
        coachId: Int,
        activityId: Int,
    ): RemoveWaterActivityResult {
        return transactionManager.run {
            val athleteRepository = it.athleteRepository
            val activityRepository = it.activityRepository

            val activity =
                activityRepository.getActivityById(activityId)
                    ?: return@run failure(RemoveWaterActivityError.ActivityNotFound)

            if (activity.type != ActivityType.WATER) {
                return@run failure(RemoveWaterActivityError.NotWaterActivity)
            }

            val athlete = athleteRepository.getAthlete(activity.uid)!!

            if (athlete.coach != coachId) return@run failure(RemoveWaterActivityError.NotAthletesCoach)

            activityRepository.removeActivity(activityId)

            success(activityId)
        }
    }

    fun createQuestionnaire(
        uid: Int,
        activityId: Int,
        sleep: Int,
        fatigue: Int,
        stress: Int,
        musclePain: Int,
    ): CreateQuestionnaireResult {
        return transactionManager.run {
            val athleteRepository = it.athleteRepository
            val activityRepository = it.activityRepository
            val waterActivityRepository = it.waterActivityRepository

            val activity =
                activityRepository.getActivityById(activityId)
                    ?: return@run failure(CreateQuestionnaireError.ActivityNotFound)

            val athlete = athleteRepository.getAthlete(activity.uid)!!

            if (uid != athlete.uid && uid != athlete.coach) {
                return@run failure(CreateQuestionnaireError.NotAthletesCoach)
            }

            if (waterActivityRepository.getQuestionnaire(activityId) != null) {
                return@run failure(CreateQuestionnaireError.AlreadyExists)
            }

            if (!waterActivityDomain.checkQuestionnaireValue(sleep)) {
                return@run failure(CreateQuestionnaireError.InvalidSleep)
            }

            if (!waterActivityDomain.checkQuestionnaireValue(fatigue)) {
                return@run failure(CreateQuestionnaireError.InvalidFatigue)
            }

            if (!waterActivityDomain.checkQuestionnaireValue(stress)) {
                return@run failure(CreateQuestionnaireError.InvalidStress)
            }

            if (!waterActivityDomain.checkQuestionnaireValue(musclePain)) {
                return@run failure(CreateQuestionnaireError.InvalidMusclePain)
            }

            waterActivityRepository.storeQuestionnaire(activityId, sleep, fatigue, stress, musclePain)

            success(Unit)
        }
    }

    fun getQuestionnaire(
        uid: Int,
        activityId: Int,
    ): GetQuestionnaireResult {
        return transactionManager.run {
            val athleteRepository = it.athleteRepository
            val activityRepository = it.activityRepository
            val waterActivityRepository = it.waterActivityRepository

            val activity =
                activityRepository.getActivityById(activityId)
                    ?: return@run failure(GetQuestionnaireError.ActivityNotFound)

            val athlete = athleteRepository.getAthlete(activity.uid)!!

            if (uid != athlete.uid && uid != athlete.coach) {
                return@run failure(GetQuestionnaireError.NotAthletesCoach)
            }

            val questionnaire =
                waterActivityRepository.getQuestionnaire(activityId)
                    ?: return@run failure(GetQuestionnaireError.QuestionnaireNotFound)

            success(questionnaire)
        }
    }
}

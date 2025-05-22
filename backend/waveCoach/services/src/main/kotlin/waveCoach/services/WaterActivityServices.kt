package waveCoach.services

import org.springframework.stereotype.Component
import waveCoach.domain.WaterActivityDomain
import waveCoach.repository.TransactionManager
import waveCoach.utils.Either
import waveCoach.utils.failure
import waveCoach.utils.success

data class WaveInputInfo(
    val points: Float?,
    val maneuvers: List<ManeuverInputInfo>,
)

data class ManeuverInputInfo(
    val waterManeuverId: Int,
    val rightSide: Boolean,
    val success: Boolean,
)

sealed class CreateWaterActivityError {
    data object InvalidDate : CreateWaterActivityError()

    data object AthleteNotFound : CreateWaterActivityError()

    data object NotAthletesCoach : CreateWaterActivityError()

    data object ActivityWithoutMicrocycle : CreateWaterActivityError()

    data object InvalidPse : CreateWaterActivityError()

    data object InvalidHeartRate : CreateWaterActivityError()

    data object InvalidDuration : CreateWaterActivityError()

    data object InvalidWaterManeuver : CreateWaterActivityError()
}
typealias CreateWaterActivityResult = Either<CreateWaterActivityError, Int>

@Component
class WaterActivityServices(
    private val transactionManager: TransactionManager,
    private val waterActivityDomain: WaterActivityDomain
) {
    fun createWaterActivity(
        coachId: Int,
        athleteId: Int,
        date: String,
        pse: Int,
        condition: String,
        heartRate: Int,
        duration: Int,
        waves: List<WaveInputInfo>,
    ): CreateWaterActivityResult {
        val dateLong = dateToLong(date) ?: return failure(CreateWaterActivityError.InvalidDate)

        return transactionManager.run {
            val athleteRepository = it.athleteRepository
            val activityRepository = it.activityRepository
            val waterActivityRepository = it.waterActivityRepository
            val waterManeuverRepository = it.waterManeuverRepository

            val athlete = athleteRepository.getAthlete(athleteId)
                ?: return@run failure(CreateWaterActivityError.AthleteNotFound)

            if (athlete.coach != coachId)
                return@run failure(CreateWaterActivityError.NotAthletesCoach)

            val micro = activityRepository.getMicrocycleByDate(dateLong, athleteId)
                ?: return@run failure(CreateWaterActivityError.ActivityWithoutMicrocycle)

            val activityId = activityRepository.storeActivity(athleteId, dateLong, micro.id)

            if (!waterActivityDomain.checkPse(pse))
                return@run failure(CreateWaterActivityError.InvalidPse)

            if (!waterActivityDomain.checkHeartRate(heartRate))
                return@run failure(CreateWaterActivityError.InvalidHeartRate)

            if (!waterActivityDomain.checkDuration(duration))
                return@run failure(CreateWaterActivityError.InvalidDuration)

            val waterActivityId = waterActivityRepository.storeWaterActivity(
                activityId,
                pse,
                condition,
                heartRate,
                duration,
            )

            waves.forEachIndexed { waveOrder, wave ->
                val waveId = waterActivityRepository.storeWave(
                    waterActivityId,
                    wave.points,
                    waveOrder
                )
                wave.maneuvers.forEachIndexed { maneuverOrder, maneuver ->
                    if (!waterManeuverRepository.isWaterManeuverValid(maneuver.waterManeuverId))
                        return@run failure(CreateWaterActivityError.InvalidWaterManeuver)

                    waterActivityRepository.storeManeuver(
                        waveId,
                        maneuver.waterManeuverId,
                        maneuver.rightSide,
                        maneuver.success,
                        maneuverOrder
                    )
                }
            }

            success(waterActivityId)
        }
    }
}
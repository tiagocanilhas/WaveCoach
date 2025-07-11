package waveCoach.services

import org.springframework.stereotype.Component
import waveCoach.domain.ActivityType
import waveCoach.domain.ManeuverToInsert
import waveCoach.domain.ManeuverToUpdate
import waveCoach.domain.Questionnaire
import waveCoach.domain.WaterActivityDomain
import waveCoach.domain.WaterActivityWithWaves
import waveCoach.domain.WaveToInsert
import waveCoach.domain.WaveToUpdate
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

data class UpdateWaveInputInfo(
    val id: Int?,
    val points: Float?,
    val rightSide: Boolean?,
    val order: Int?,
    val maneuvers: List<UpdateManeuverInputInfo>?,
)

data class UpdateManeuverInputInfo(
    val id: Int?,
    val waterManeuverId: Int?,
    val success: Boolean?,
    val order: Int?,
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

sealed class GetLastWaterActivityError {
    data object AthleteNotFound : GetLastWaterActivityError()

    data object NotAthletesCoach : GetLastWaterActivityError()

    data object ActivityNotFound : GetLastWaterActivityError()
}
typealias GetLastWaterActivityResult = Either<GetLastWaterActivityError, WaterActivityWithWaves>

sealed class RemoveWaterActivityError {
    data object ActivityNotFound : RemoveWaterActivityError()

    data object NotAthletesCoach : RemoveWaterActivityError()

    data object NotWaterActivity : RemoveWaterActivityError()
}
typealias RemoveWaterActivityResult = Either<RemoveWaterActivityError, Int>

sealed class UpdateWaterActivityError {
    data object ActivityNotFound : UpdateWaterActivityError()

    data object NotAthletesCoach : UpdateWaterActivityError()

    data object NotWaterActivity : UpdateWaterActivityError()

    data object InvalidRpe : UpdateWaterActivityError()

    data object InvalidTrimp : UpdateWaterActivityError()

    data object InvalidDuration : UpdateWaterActivityError()

    data object InvalidDate : UpdateWaterActivityError()

    data object InvalidWaveOrder : UpdateWaterActivityError()

    data object InvalidRightSide : UpdateWaterActivityError()

    data object InvalidManeuvers : UpdateWaterActivityError()

    data object InvalidWaterManeuver : UpdateWaterActivityError()

    data object InvalidSuccess : UpdateWaterActivityError()

    data object WaveNotFound : UpdateWaterActivityError()

    data object ManeuverNotFound : UpdateWaterActivityError()

    data object InvalidManeuverOrder : UpdateWaterActivityError()
}
typealias UpdateWaterActivityResult = Either<UpdateWaterActivityError, Int>

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
        rpe: Int,
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

            if (!waterActivityDomain.checkRpe(rpe)) {
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
                    rpe,
                    condition,
                    trimp,
                    duration,
                )

            val wavesToInsert =
                waves.mapIndexed { waveOrder, wave ->
                    WaveToInsert(
                        waterActivityId,
                        wave.points,
                        wave.rightSide,
                        waveOrder + 1,
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
                        maneuverOrder + 1,
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

    fun getLastWaterActivity(
        uid: Int,
        athleteId: Int,
    ): GetLastWaterActivityResult {
        return transactionManager.run {
            val athleteRepository = it.athleteRepository
            val waterActivityRepository = it.waterActivityRepository

            val athlete = athleteRepository.getAthlete(athleteId)
                ?: return@run failure(GetLastWaterActivityError.AthleteNotFound)

            if (athlete.uid != uid && athlete.coach != uid) {
                return@run failure(GetLastWaterActivityError.NotAthletesCoach)
            }

            val activity = waterActivityRepository.getLastWaterActivity(athlete.uid)
                ?: return@run failure(GetLastWaterActivityError.ActivityNotFound)

            success(activity)
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

    fun updateWaterActivity(
        coachId: Int,
        activityId: Int,
        date: String?,
        rpe: Int?,
        condition: String?,
        trimp: Int?,
        duration: Int?,
        waves: List<UpdateWaveInputInfo>?,
    ): UpdateWaterActivityResult {
        val dateLong = date?.let { dateToLong(it) ?: return failure(UpdateWaterActivityError.InvalidDate) }

        return transactionManager.run { it ->
            val athleteRepository = it.athleteRepository
            val activityRepository = it.activityRepository
            val waterActivityRepository = it.waterActivityRepository
            val waterManeuverRepository = it.waterManeuverRepository

            val activity = activityRepository.getActivityById(activityId)
                ?: return@run failure(UpdateWaterActivityError.ActivityNotFound)

            if (activity.type != ActivityType.WATER) return@run failure(UpdateWaterActivityError.NotWaterActivity)

            val athlete = athleteRepository.getAthlete(activity.uid)!!

            if (athlete.coach != coachId) return@run failure(UpdateWaterActivityError.NotAthletesCoach)

            if (dateLong != null && dateLong != activity.date)
                activityRepository.updateActivity(activityId, dateLong)

            if (rpe != null || condition != null || trimp != null || duration != null) {

                if (rpe != null && !waterActivityDomain.checkRpe(rpe))
                    return@run failure(UpdateWaterActivityError.InvalidRpe)

                if (trimp != null && !waterActivityDomain.checkTrimp(trimp))
                    return@run failure(UpdateWaterActivityError.InvalidTrimp)

                if (duration != null && !waterActivityDomain.checkDuration(duration))
                    return@run failure(UpdateWaterActivityError.InvalidDuration)

                waterActivityRepository.updateWaterActivity(activityId, rpe, condition, trimp, duration)
            }

            if (waves != null) {
                val (create, update, delete) = separateCreateUpdateDelete(waves)

                val wavesOnDB = waterActivityRepository.getWavesByActivity(activityId)

                // Update existing waves
                val wavesToUpdate = update.map { wave ->
                    if (wave.order != null &&
                        (wave.order <= 0 || !checkOrderConflict(wavesOnDB, update, "waveOrder", wave.order))
                    )
                        return@run failure(UpdateWaterActivityError.InvalidWaveOrder)

                    if (wavesOnDB.none { it.id == wave.id })
                        return@run failure(UpdateWaterActivityError.WaveNotFound)

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

                update.forEachIndexed { waveIndex, wave ->
                    if (wave.maneuvers != null) {
                        val (maneuverCreate, maneuverUpdate, maneuverDelete) =
                            separateCreateUpdateDelete(wave.maneuvers)

                        val maneuversOnDB = waterActivityRepository.getManeuversByWave(update[waveIndex].id!!)

                        // Update existing Maneuvers
                        maneuverUpdate.forEach {
                            if (it.waterManeuverId != null && it.waterManeuverId <= 0 &&
                                waterManeuverRepository.getWaterManeuverById(it.waterManeuverId) != null
                            )
                                return@run failure(UpdateWaterActivityError.InvalidWaterManeuver)

                            if (maneuversOnDB.none { maneuver -> maneuver.id == it.id })
                                return@run failure(UpdateWaterActivityError.ManeuverNotFound)

                            if (it.order != null && (it.order <= 0 || !checkOrderConflict(
                                    maneuversOnDB, maneuverUpdate, "maneuverOrder", it.order
                                ))
                            )
                                return@run failure(UpdateWaterActivityError.InvalidManeuverOrder)

                            maneuversUpdate.add(
                                ManeuverToUpdate(
                                    it.id!!,
                                    it.waterManeuverId,
                                    it.success,
                                    it.order
                                )
                            )
                        }
                        waterActivityRepository.updateManeuvers(maneuversUpdate)

                        // Add New Maneuvers
                        maneuverCreate.forEach { maneuver ->
                            if (maneuver.waterManeuverId == null || maneuver.waterManeuverId <= 0 ||
                                waterManeuverRepository.getWaterManeuverById(maneuver.waterManeuverId) == null
                            )
                                return@run failure(UpdateWaterActivityError.InvalidWaterManeuver)

                            if (maneuver.success == null)
                                return@run failure(UpdateWaterActivityError.InvalidSuccess)

                            if (maneuver.order == null || maneuver.order <= 0)
                                return@run failure(UpdateWaterActivityError.InvalidManeuverOrder)

                            maneuversCreate.add(
                                ManeuverToInsert(
                                    update[waveIndex].id!!,
                                    maneuver.waterManeuverId,
                                    maneuver.success,
                                    maneuver.order,
                                )
                            )

                        }

                        // Delete Maneuvers
                        if (maneuverDelete.any { id -> maneuversOnDB.none { it.id == id } })
                            return@run failure(UpdateWaterActivityError.ManeuverNotFound)

                        maneuversDelete.addAll(maneuverDelete)
                    }
                    waterActivityRepository.updateWaves(wavesToUpdate)

                    waterActivityRepository.removeManeuversById(maneuversDelete)
                }

                // Add New waves
                val wavesToInsert = create.map { wave ->
                    if (wave.order == null || wave.order <= 0 ||
                        waterActivityRepository.verifyWaveOrder(activityId, wave.order)
                    )
                        return@run failure(UpdateWaterActivityError.InvalidWaveOrder)

                    if (wave.rightSide == null) return@run failure(UpdateWaterActivityError.InvalidRightSide)

                    WaveToInsert(
                        activityId,
                        wave.points,
                        wave.rightSide,
                        wave.order,
                    )
                }

                val wavesCreatedIds = waterActivityRepository.storeWaves(wavesToInsert)

                val maneuversToInsert = create.flatMapIndexed { wavesIndex, wave ->
                    if (wave.maneuvers == null) return@run failure(UpdateWaterActivityError.InvalidManeuvers)

                    wave.maneuvers.mapIndexed { maneuverIndex, maneuver ->
                        if (maneuver.waterManeuverId == null || maneuver.waterManeuverId <= 0)
                            return@run failure(UpdateWaterActivityError.InvalidWaterManeuver)

                        if (maneuver.success == null)
                            return@run failure(UpdateWaterActivityError.InvalidSuccess)

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
                if (delete.any { id -> wavesOnDB.none { it.id == id } })
                    return@run failure(UpdateWaterActivityError.WaveNotFound)
                waterActivityRepository.removeWavesById(delete)
            }

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

package waveCoach.repository

import waveCoach.domain.Maneuver
import waveCoach.domain.ManeuverToInsert
import waveCoach.domain.ManeuverToUpdate
import waveCoach.domain.MesocycleWater
import waveCoach.domain.Questionnaire
import waveCoach.domain.WaterActivityWithWaves
import waveCoach.domain.Wave
import waveCoach.domain.WaveToInsert
import waveCoach.domain.WaveToUpdate

interface WaterActivityRepository {
    // Activity methods
    fun storeWaterActivity(
        activityId: Int,
        rpe: Int,
        condition: String,
        trimp: Int,
        duration: Int,
    ): Int

    fun isWaterActivityValid(activityId: Int): Boolean

    fun getWaterActivities(uid: Int): List<MesocycleWater>

    fun getWaterActivity(activityId: Int): WaterActivityWithWaves?

    fun getLastWaterActivity(athleteId: Int): WaterActivityWithWaves?

    fun updateWaterActivity(
        activityId: Int,
        rpe: Int?,
        condition: String?,
        trimp: Int?,
        duration: Int?,
    )

    fun removeWaterActivity(activityId: Int)

    fun removeWaterActivities(athleteId: Int)

    // Wave methods
    fun storeWaves(waves: List<WaveToInsert>): List<Int>

    fun storeWave(
        activityId: Int,
        points: Float?,
        rightSide: Boolean,
        order: Int,
    ): Int

    fun getWaves(activityId: Int): List<Wave>

    fun getWaveById(waveId: Int): Wave?

    fun getWavesByActivity(activityId: Int): List<Wave>

    fun updateWaves(waves: List<WaveToUpdate>)

    fun removeWavesByActivity(activityId: Int)

    fun removeWavesByAthlete(athleteId: Int)

    fun removeWavesById(waveIds: List<Int>)

    fun removeWaveById(waveId: Int)

    fun verifyWaveOrder(
        activityId: Int,
        order: Int,
    ): Boolean

    // Maneuver methods
    fun storeManeuver(
        waveId: Int,
        waterManeuverId: Int,
        success: Boolean,
        order: Int,
    ): Int

    fun storeManeuvers(maneuvers: List<ManeuverToInsert>): List<Int>

    fun getManeuversByWave(waveId: Int): List<Maneuver>

    fun getManeuverById(maneuverId: Int): Maneuver?

    fun updateManeuvers(maneuvers: List<ManeuverToUpdate>)

    fun removeManeuversByActivity(activityId: Int)

    fun removeManeuversByAthlete(athleteId: Int)

    fun removeManeuversById(maneuverIds: List<Int>)

    fun removeManeuverById(maneuverId: Int)

    fun verifyManeuverOrder(
        waveId: Int,
        order: Int,
    ): Boolean

    // Questionnaire methods
    fun storeQuestionnaire(
        activityId: Int,
        sleep: Int,
        fatigue: Int,
        stress: Int,
        musclePain: Int,
    )

    fun getQuestionnaire(activityId: Int): Questionnaire?

    fun removeQuestionnaire(activityId: Int)
}

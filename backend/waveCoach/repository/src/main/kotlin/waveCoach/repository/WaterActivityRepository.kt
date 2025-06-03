package waveCoach.repository

import waveCoach.domain.MesocycleWater
import waveCoach.domain.Questionnaire
import waveCoach.domain.WaterActivityWithWaves

interface WaterActivityRepository {
    fun storeWaterActivity(
        activityId: Int,
        rpe: Int,
        condition: String,
        trimp: Int,
        duration: Int,
    ): Int

    fun removeWaterActivity(activityId: Int)

    fun removeWaterActivities(athleteId: Int)

    fun storeWave(
        activityId: Int,
        points: Float?,
        rightSide: Boolean,
        order: Int,
    ): Int

    fun removeWavesByActivity(activityId: Int)

    fun removeWavesByAthlete(athleteId: Int)

    fun storeManeuver(
        waveId: Int,
        waterManeuverId: Int,
        success: Boolean,
        order: Int,
    ): Int

    fun removeManeuversByActivity(activityId: Int)

    fun removeManeuversByAthlete(athleteId: Int)

    fun getWaterActivity(activityId: Int): WaterActivityWithWaves?

    fun getWaterActivities(uid: Int): List<MesocycleWater>

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

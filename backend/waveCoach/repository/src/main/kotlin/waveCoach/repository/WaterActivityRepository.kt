package waveCoach.repository

import waveCoach.domain.WaterActivityWithWaves

interface WaterActivityRepository {
    fun storeWaterActivity(activityId: Int, pse: Int, condition: String, heartRate: Int, duration: Int): Int

    fun removeWaterActivity(activityId: Int)

    fun removeWaterActivities(athleteId: Int)

    fun storeWave(activityId: Int, points: Float?, order: Int): Int

    fun removeWavesByActivity(activityId: Int)

    fun removeWavesByAthlete(athleteId: Int)

    fun storeManeuver(waveId: Int, waterManeuverId: Int, rightSide: Boolean, success: Boolean, order: Int): Int

    fun removeManeuversByActivity(activityId: Int)

    fun removeManeuversByAthlete(athleteId: Int)

    fun getWaterActivity(activityId: Int): WaterActivityWithWaves?

}
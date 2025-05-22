package waveCoach.repository

interface WaterActivityRepository {
    fun storeWaterActivity(activityId: Int, pse: Int, condition: String, heartRate: Int, duration: Int): Int

    fun storeWave(activityId: Int, points: Float?, order: Int): Int

    fun storeManeuver(waveId: Int, waterManeuverId: Int, rightSide: Boolean, success: Boolean, order: Int): Int

}
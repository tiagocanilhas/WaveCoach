package waveCoach.repository

import waveCoach.domain.*


interface WaterActivityRepository {
    fun storeWaterActivity(activityId: Int, rpe: Int, condition: String, trimp: Int, duration: Int): Int

    fun isWaterActivityValid(activityId: Int): Boolean

    fun updateWaterActivity(activityId: Int, rpe: Int?, condition: String?, trimp: Int?, duration: Int?)

    fun removeWaterActivity(activityId: Int)

    fun removeWaterActivities(athleteId: Int)

    fun storeWave(activityId: Int, points: Float?, rightSide: Boolean, order: Int): Int

    fun getWaveById(waveId: Int): Wave?

    fun getWavesByActivity(activityId: Int): List<Wave>

    fun storeWaves(waves: List<WaveToInsert>): List<Int>

    fun updateWaves(waves: List<WaveToUpdate>)

    fun removeWavesByActivity(activityId: Int)

    fun removeWavesByAthlete(athleteId: Int)

    fun removeWaveById(waveId: Int)

    fun removeWavesById(waveIds: List<Int>)

    fun verifyWaveOrder(activityId: Int, order: Int): Boolean

    fun storeManeuver(waveId: Int, waterManeuverId: Int, success: Boolean, order: Int): Int

    fun storeManeuvers(maneuvers: List<ManeuverToInsert>): List<Int>

    fun getManeuverById(maneuverId: Int): Maneuver?

    fun getManeuversByWave(waveId: Int): List<Maneuver>

    fun updateManeuvers(maneuvers: List<ManeuverToUpdate>)

    fun removeManeuversByActivity(activityId: Int)

    fun removeManeuversByAthlete(athleteId: Int)

    fun removeManeuverById(maneuverId: Int)

    fun removeManeuversById(maneuverIds: List<Int>)

    fun verifyManeuverOrder(waveId: Int, order: Int): Boolean

    fun getWaterActivity(activityId: Int): WaterActivityWithWaves?

    fun getWaterActivities(uid: Int): List<MesocycleWater>

    fun storeQuestionnaire(activityId: Int, sleep: Int, fatigue: Int, stress: Int, musclePain: Int)

    fun getQuestionnaire(activityId: Int): Questionnaire?

    fun removeQuestionnaire(activityId: Int)
}

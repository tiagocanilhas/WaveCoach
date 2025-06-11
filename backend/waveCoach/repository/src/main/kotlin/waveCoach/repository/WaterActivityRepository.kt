package waveCoach.repository

import waveCoach.domain.ManeuverToInsert
import waveCoach.domain.MesocycleWater
import waveCoach.domain.Questionnaire
import waveCoach.domain.WaterActivityWithWaves
import waveCoach.domain.WaveToInsert
import waveCoach.domain.Wave
import waveCoach.domain.Maneuver


interface WaterActivityRepository {
    fun storeWaterActivity(activityId: Int, rpe: Int, condition: String, trimp: Int, duration: Int): Int

    fun isWaterActivityValid(activityId: Int): Boolean

    fun removeWaterActivity(activityId: Int)

    fun removeWaterActivities(athleteId: Int)

    fun storeWave(activityId: Int, points: Float?, rightSide: Boolean, order: Int): Int

    fun getWaveById(waveId: Int): Wave?

    fun storeWaves(waves: List<WaveToInsert>): List<Int>

    fun removeWavesByActivity(activityId: Int)

    fun removeWavesByAthlete(athleteId: Int)

    fun removeWaveById(waveId: Int)

    fun verifyWaveOrder(activityId: Int, order: Int): Boolean

    fun storeManeuver(waveId: Int, waterManeuverId: Int, success: Boolean, order: Int): Int

    fun storeManeuvers(maneuvers: List<ManeuverToInsert>): List<Int>

    fun getManeuverById(maneuverId: Int): Maneuver?

    fun storeManeuvers(maneuvers: List<ManeuverToInsert>): List<Int>

    fun removeManeuversByActivity(activityId: Int)

    fun removeManeuversByAthlete(athleteId: Int)

    fun removeManeuverById(maneuverId: Int)

    fun verifyManeuverOrder(waveId: Int, order: Int): Boolean

    fun getWaterActivity(activityId: Int): WaterActivityWithWaves?

    fun getWaterActivities(uid: Int): List<MesocycleWater>

    fun storeQuestionnaire(activityId: Int, sleep: Int, fatigue: Int, stress: Int, musclePain: Int)

    fun getQuestionnaire(activityId: Int): Questionnaire?

    fun removeQuestionnaire(activityId: Int)
}

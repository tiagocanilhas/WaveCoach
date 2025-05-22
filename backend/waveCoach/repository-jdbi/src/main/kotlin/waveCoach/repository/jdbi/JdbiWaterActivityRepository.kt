package waveCoach.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import waveCoach.repository.WaterActivityRepository

class JdbiWaterActivityRepository(
    private val handle: Handle
) : WaterActivityRepository {
    override fun storeWaterActivity(activityId: Int, pse: Int, condition: String, heartRate: Int, duration: Int): Int =
        handle.createUpdate(
            """
            insert into waveCoach.water (activity, pse, condition, heart_rate, duration) 
            values (:activityId, :pse, :condition, :heartRate, :duration)
            """.trimIndent(),
        )
            .bind("activityId", activityId)
            .bind("pse", pse)
            .bind("condition", condition)
            .bind("heartRate", heartRate)
            .bind("duration", duration)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .one()

    override fun storeWave(activityId: Int, points: Float?, order: Int): Int =
        handle.createUpdate(
            """
            insert into waveCoach.wave (activity, points, wave_order) 
            values (:activityId, :points, :order)
            """.trimIndent(),
        )
            .bind("activityId", activityId)
            .bind("points", points)
            .bind("order", order)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .one()

    override fun storeManeuver(
        waveId: Int,
        waterManeuverId: Int,
        rightSide: Boolean,
        success: Boolean,
        order: Int
    ): Int =
        handle.createUpdate(
            """
            insert into waveCoach.maneuver (wave, maneuver, right_side, success, maneuver_order) 
            values (:waveId, :waterManeuverId, :rightSide, :success, :order)
            """.trimIndent(),
        )
            .bind("waveId", waveId)
            .bind("waterManeuverId", waterManeuverId)
            .bind("rightSide", rightSide)
            .bind("success", success)
            .bind("order", order)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .one()
}
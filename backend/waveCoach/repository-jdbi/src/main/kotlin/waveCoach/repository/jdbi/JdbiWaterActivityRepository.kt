package waveCoach.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import waveCoach.domain.WaterActivityWithWaves
import waveCoach.domain.WaveWithManeuvers
import waveCoach.repository.WaterActivityRepository
import waveCoach.domain.Maneuver

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

    private data class Row(
        val activityId: Int,
        val uid: Int,
        val microcycle: Int,
        val date: Long,
        val pse: Int,
        val condition: String,
        val heartRate: Int,
        val duration: Int,
        val waveId: Int?,
        val points: Float?,
        val waveOrder: Int?,
        val maneuverId: Int?,
        val waterManeuverId: Int?,
        val waterManeuverName: String?,
        val url: String?,
        val rightSide: Boolean?,
        val success: Boolean?,
        val maneuverOrder: Int?,
    )

    override fun getWaterActivity(activityId: Int): WaterActivityWithWaves? {
        val query = """
            select a.id as activity_id, a.uid, a.microcycle, a.date, w.pse, w.condition, w.heart_rate, w.duration,
            wv.id as wave_id, wv.points, wv.wave_order, m.id as maneuver_id, m.right_side, m.success, m.maneuver_order,
            wm.id as water_maneuver_id, wm.name as water_maneuver_name, wm.url
            from waveCoach.activity a
            left join waveCoach.water w on a.id = w.activity
            left join waveCoach.wave wv on w.activity = wv.activity
            left join waveCoach.maneuver m on wv.id = m.wave
            left join waveCoach.water_maneuver wm on m.maneuver = wm.id
            where a.id = :activityId
            order by wv.wave_order, m.maneuver_order
        """.trimIndent()

        val rows = handle.createQuery(query)
            .bind("activityId", activityId)
            .mapTo<Row>()

        val activity = rows.firstOrNull() ?: return null

        return WaterActivityWithWaves(
            id = activity.activityId,
            athleteId = activity.uid,
            microcycleId = activity.microcycle,
            date = activity.date,
            pse = activity.pse,
            condition = activity.condition,
            heartRate = activity.heartRate,
            duration = activity.duration,
            waves = rows.groupBy { it.waveId }.filterKeys { it != null }.map { (_, waveRows) ->
                val info = waveRows.first()
                WaveWithManeuvers(
                    id = info.waveId!!,
                    points = info.points,
                    order = info.waveOrder!!,
                    maneuvers = waveRows.mapNotNull { maneuverRow ->
                        if (maneuverRow.maneuverId != null) {
                            Maneuver(
                                id = maneuverRow.maneuverId,
                                waterManeuverId = maneuverRow.waterManeuverId!!,
                                waterManeuverName = maneuverRow.waterManeuverName!!,
                                url = maneuverRow.url,
                                rightSide = maneuverRow.rightSide!!,
                                success = maneuverRow.success!!,
                                order = maneuverRow.maneuverOrder!!
                            )
                        } else null
                    }.ifEmpty { emptyList() }
                )
            }.ifEmpty { emptyList() }
        )
    }

}

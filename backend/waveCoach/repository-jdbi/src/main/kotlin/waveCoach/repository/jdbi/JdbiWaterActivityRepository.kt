package waveCoach.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import waveCoach.domain.Maneuver
import waveCoach.domain.ManeuverToInsert
import waveCoach.domain.ManeuverToUpdate
import waveCoach.domain.MesocycleWater
import waveCoach.domain.MicrocycleWater
import waveCoach.domain.Questionnaire
import waveCoach.domain.WaterActivityWithWaves
import waveCoach.domain.WaveToInsert
import waveCoach.domain.WaveWithManeuvers
import waveCoach.domain.Wave
import waveCoach.domain.WaveToUpdate
import waveCoach.repository.WaterActivityRepository

class JdbiWaterActivityRepository(
    private val handle: Handle,
) : WaterActivityRepository {
    // Activity methods
    override fun storeWaterActivity(
        activityId: Int,
        rpe: Int,
        condition: String,
        trimp: Int,
        duration: Int,
    ): Int =
        handle.createUpdate(
            """
            insert into waveCoach.water (activity, rpe, condition, trimp, duration) 
            values (:activityId, :rpe, :condition, :trimp, :duration)
            """.trimIndent(),
        )
            .bind("activityId", activityId)
            .bind("rpe", rpe)
            .bind("condition", condition)
            .bind("trimp", trimp)
            .bind("duration", duration)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .one()

    override fun isWaterActivityValid(activityId: Int): Boolean =
        handle.createQuery("select count(*) from waveCoach.water where activity = :activityId")
            .bind("activityId", activityId)
            .mapTo<Int>()
            .one() > 0

    override fun getWaterActivity(activityId: Int): WaterActivityWithWaves? {
        val query =
            """
            select a.id as activity_id, a.uid, a.microcycle, a.date, w.rpe, w.condition, w.trimp, w.duration,
            wv.id as wave_id, wv.points, wv.right_side, wv.wave_order, m.id as maneuver_id, m.success, m.maneuver_order,
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
            .list()

        val activity = rows.firstOrNull() ?: return null

        return WaterActivityWithWaves(
            id = activity.activityId,
            athleteId = activity.uid,
            microcycleId = activity.microcycle,
            date = activity.date,
            rpe = activity.rpe,
            condition = activity.condition,
            trimp = activity.trimp,
            duration = activity.duration,
            waves =
                rows.groupBy { it.waveId }.filterKeys { it != null }.map { (_, waveRows) ->
                    val info = waveRows.first()
                    WaveWithManeuvers(
                        id = info.waveId!!,
                        points = info.points,
                        order = info.waveOrder!!,
                        rightSide = info.rightSide!!,
                        maneuvers =
                            waveRows.mapNotNull { maneuverRow ->
                                if (maneuverRow.maneuverId != null) {
                                    Maneuver(
                                        id = maneuverRow.maneuverId,
                                        wave = info.waveId,
                                        waterManeuverId = maneuverRow.waterManeuverId!!,
                                        waterManeuverName = maneuverRow.waterManeuverName!!,
                                        url = maneuverRow.url,
                                        success = maneuverRow.success!!,
                                        maneuverOrder = maneuverRow.maneuverOrder!!,
                                    )
                                } else {
                                    null
                                }
                            }.ifEmpty { emptyList() },
                    )
                }.ifEmpty { emptyList() },
        )
    }

    override fun getLastWaterActivity(athleteId: Int): WaterActivityWithWaves? {
        val query =
            """
            select a.id as activity_id, a.uid, a.microcycle, a.date, w.rpe, w.condition, w.trimp, w.duration,
            wv.id as wave_id, wv.points, wv.right_side, wv.wave_order, m.id as maneuver_id, m.success, m.maneuver_order,
            wm.id as water_maneuver_id, wm.name as water_maneuver_name, wm.url
            from waveCoach.activity a
            left join waveCoach.water w on a.id = w.activity
            left join waveCoach.wave wv on w.activity = wv.activity
            left join waveCoach.maneuver m on wv.id = m.wave
            left join waveCoach.water_maneuver wm on m.maneuver = wm.id
			where a.id = (
			  select a2.id from waveCoach.activity a2
			  where a2.uid = :athleteId and a2.type = 'water'
			  order by a2.date desc
			  limit 1
			)
        """.trimIndent()

        val rows = handle.createQuery(query)
            .bind("athleteId", athleteId)
            .mapTo<Row>()
            .list()

        val activity = rows.firstOrNull() ?: return null

        return WaterActivityWithWaves(
            id = activity.activityId,
            athleteId = activity.uid,
            microcycleId = activity.microcycle,
            date = activity.date,
            rpe = activity.rpe,
            condition = activity.condition,
            trimp = activity.trimp,
            duration = activity.duration,
            waves =
                rows.groupBy { it.waveId }.filterKeys { it != null }.map { (_, waveRows) ->
                    val info = waveRows.first()
                    WaveWithManeuvers(
                        id = info.waveId!!,
                        points = info.points,
                        order = info.waveOrder!!,
                        rightSide = info.rightSide!!,
                        maneuvers =
                            waveRows.mapNotNull { maneuverRow ->
                                if (maneuverRow.maneuverId != null) {
                                    Maneuver(
                                        id = maneuverRow.maneuverId,
                                        wave = info.waveId,
                                        waterManeuverId = maneuverRow.waterManeuverId!!,
                                        waterManeuverName = maneuverRow.waterManeuverName!!,
                                        url = maneuverRow.url,
                                        success = maneuverRow.success!!,
                                        maneuverOrder = maneuverRow.maneuverOrder!!,
                                    )
                                } else {
                                    null
                                }
                            }.ifEmpty { emptyList() },
                    )
                }.ifEmpty { emptyList() },
        )
    }

    override fun getWaterActivities(uid: Int): List<MesocycleWater> {
        val query =
            """
            select meso.id as meso_id, meso.start_time as meso_start, meso.end_time as meso_end,
				micro.id as micro_id, micro.start_time as micro_start, micro.end_time as micro_end, 
				a.id as activity_id, a.uid, a.microcycle, a.date, w.rpe, w.condition, w.trimp, w.duration, 
                wv.id as wave_id, wv.points, wv.right_side, wv.wave_order, m.id as maneuver_id,
                m.success, m.maneuver_order, wm.id as water_maneuver_id, wm.name as water_maneuver_name,
                wm.url
            from waveCoach.mesocycle meso
            left join waveCoach.microcycle micro on micro.mesocycle = meso.id
            left join waveCoach.activity a on a.microcycle = micro.id
            left join waveCoach.water w on a.id = w.activity
            left join waveCoach.wave wv on w.activity = wv.activity
            left join waveCoach.maneuver m on wv.id = m.wave
            left join waveCoach.water_maneuver wm on m.maneuver = wm.id
            where a.type = 'water' and meso.uid = :uid 
            order by meso.start_time, micro.start_time, a.date, wv.wave_order, m.maneuver_order
        """.trimIndent()

        data class Row(
            val mesoId: Int,
            val mesoStart: Long,
            val mesoEnd: Long,
            val microId: Int?,
            val microStart: Long?,
            val microEnd: Long?,
            val activityId: Int?,
            val uid: Int,
            val microcycle: Int,
            val date: Long,
            val rpe: Int?,
            val condition: String?,
            val trimp: Int?,
            val duration: Int?,
            val waveId: Int?,
            val points: Float?,
            val rightSide: Boolean?,
            val waveOrder: Int?,
            val maneuverId: Int?,
            val success: Boolean?,
            val maneuverOrder: Int?,
            val waterManeuverId: Int?,
            val waterManeuverName: String?,
            val url: String?,
        )

        val rows =
            handle.createQuery(query)
                .bind("uid", uid)
                .mapTo<Row>()
                .list()

        return rows.groupBy { it.mesoId }
            .map { (mesoId, mesoRows) ->
                val mesoInfo = mesoRows.first()
                MesocycleWater(
                    id = mesoId,
                    uid = mesoInfo.uid,
                    startTime = mesoInfo.mesoStart,
                    endTime = mesoInfo.mesoEnd,
                    microcycles =
                        mesoRows
                            .filter { it.microId != null }
                            .groupBy { it.microId }
                            .map { (microId, microRows) ->
                                val microInfo = microRows.first()
                                MicrocycleWater(
                                    id = microId!!,
                                    startTime = microInfo.microStart!!,
                                    endTime = microInfo.microEnd!!,
                                    activities =
                                        microRows
                                            .filter { it.activityId != null }
                                            .groupBy { it.activityId }
                                            .map { (activityId, activityRows) ->
                                                val activityInfo = activityRows.first()
                                                WaterActivityWithWaves(
                                                    id = activityId!!,
                                                    athleteId = activityInfo.uid,
                                                    microcycleId = activityInfo.microcycle,
                                                    date = activityInfo.date,
                                                    rpe = activityInfo.rpe ?: 0,
                                                    condition = activityInfo.condition ?: "",
                                                    trimp = activityInfo.trimp ?: 0,
                                                    duration = activityInfo.duration ?: 0,
                                                    waves =
                                                        microRows
                                                            .filter { it.waveId != null }
                                                            .groupBy { it.waveId }
                                                            .map { (_, waveRows) ->
                                                                val info = waveRows.first()
                                                                WaveWithManeuvers(
                                                                    id = info.waveId!!,
                                                                    points = info.points,
                                                                    rightSide = info.rightSide!!,
                                                                    order = info.waveOrder!!,
                                                                    maneuvers =
                                                                        waveRows
                                                                            .filter { it.maneuverId != null }
                                                                            .mapNotNull { maneuverRow ->
                                                                                Maneuver(
                                                                                    id = maneuverRow.maneuverId!!,
                                                                                    wave = info.waveId,
                                                                                    waterManeuverId = maneuverRow.waterManeuverId!!,
                                                                                    waterManeuverName = maneuverRow.waterManeuverName!!,
                                                                                    url = maneuverRow.url,
                                                                                    success = maneuverRow.success!!,
                                                                                    maneuverOrder = maneuverRow.maneuverOrder!!,
                                                                                )
                                                                            },
                                                                )
                                                            },
                                                )
                                            },
                                )
                            },
                )
            }
    }

    override fun updateWaterActivity(
        activityId: Int,
        rpe: Int?,
        condition: String?,
        trimp: Int?,
        duration: Int?,
    ) {
        handle.createUpdate(
            """
            update waveCoach.water 
            set 
                rpe = coalesce(:rpe, rpe),
                condition = coalesce(:condition, condition),
                trimp = coalesce(:trimp, trimp),
                duration = coalesce(:duration, duration)
            where activity = :activityId
            """.trimIndent(),
        )
            .bind("activityId", activityId)
            .bind("rpe", rpe)
            .bind("condition", condition)
            .bind("trimp", trimp)
            .bind("duration", duration)
            .execute()
    }

    override fun removeWaterActivity(activityId: Int) {
        handle.createUpdate("delete from waveCoach.water where activity = :activityId")
            .bind("activityId", activityId)
            .execute()
    }

    override fun removeWaterActivities(athleteId: Int) {
        handle.createUpdate(
            """
            delete from waveCoach.water 
            where activity in (select id from waveCoach.activity where uid = :athleteId)
            """.trimIndent(),
        )
            .bind("athleteId", athleteId)
            .execute()
    }


    // Wave methods
    override fun storeWave(
        activityId: Int,
        points: Float?,
        rightSide: Boolean,
        order: Int,
    ): Int =
        handle.createUpdate(
            """
            insert into waveCoach.wave (activity, points, right_side, wave_order)
            values (:activityId, :points, :right_side, :order)
            """.trimIndent(),
        )
            .bind("activityId", activityId)
            .bind("points", points)
            .bind("right_side", rightSide)
            .bind("order", order)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .one()

    override fun getWaves(activityId: Int): List<Wave> =
        handle.createQuery(
            """
            select w.id, w.activity, w.points, w.right_side, w.wave_order
            from waveCoach.wave w
            where w.activity = :activityId
            order by w.wave_order
            """.trimIndent(),
        )
            .bind("activityId", activityId)
            .mapTo<Wave>()
            .list()


    override fun storeWaves(waves: List<WaveToInsert>): List<Int> =
        handle.prepareBatch(
            """
            insert into waveCoach.wave (activity, points, right_side, wave_order)
            values (:activityId, :points, :rightSide, :order)
            """.trimIndent(),
        ).use { batch ->
            waves.forEach { wave ->
                batch
                    .bind("activityId", wave.activityId)
                    .bind("points", wave.points)
                    .bind("rightSide", wave.rightSide)
                    .bind("order", wave.order)
                    .add()
            }
            batch.executeAndReturnGeneratedKeys()
                .mapTo<Int>()
                .list()
        }

    override fun getWaveById(waveId: Int): Wave? =
        handle.createQuery("select * from waveCoach.wave where id = :waveId")
            .bind("waveId", waveId)
            .mapTo<Wave>()
            .singleOrNull()

    override fun getWavesByActivity(activityId: Int): List<Wave> =
        handle.createQuery("select * from waveCoach.wave where activity = :activityId")
            .bind("activityId", activityId)
            .mapTo<Wave>()
            .list()

    override fun updateWaves(waves: List<WaveToUpdate>) {
        handle.prepareBatch(
            """
            update waveCoach.wave 
            set 
                points = coalesce(:points, points),
                right_side = coalesce(:rightSide, right_side),
                wave_order = coalesce(:order, wave_order)
            where id = :id
            """.trimIndent(),
        ).use { batch ->
            waves.forEach { wave ->
                batch.bind("id", wave.id)
                    .bind("points", wave.points)
                    .bind("rightSide", wave.rightSide)
                    .bind("order", wave.order)
                    .add()
            }
            batch.execute()
        }
    }

    override fun removeWavesByActivity(activityId: Int) {
        handle.createUpdate("delete from waveCoach.wave where activity = :activityId")
            .bind("activityId", activityId)
            .execute()
    }

    override fun removeWavesByAthlete(athleteId: Int) {
        handle.createUpdate(
            """
            delete from waveCoach.wave 
            where activity in (select id from waveCoach.activity where uid = :athleteId)
            """.trimIndent(),
        )
            .bind("athleteId", athleteId)
            .execute()
    }

    override fun removeWavesById(waveIds: List<Int>) {
        handle.prepareBatch(
            """
            delete from waveCoach.wave where id = :waveId
            """.trimIndent(),
        ).use { batch ->
            waveIds.forEach { waveId ->
                batch.bind("waveId", waveId).add()
            }
            batch.execute()
        }
    }

    override fun removeWaveById(waveId: Int) {
        handle.createUpdate("delete from waveCoach.wave where id = :waveId")
            .bind("waveId", waveId)
            .execute()
    }

    override fun verifyWaveOrder(activityId: Int, order: Int): Boolean =
        handle.createQuery(
            """
            select exists (select 1 from waveCoach.wave 
                where activity = :activityId and wave_order = :order
            )
            """.trimIndent(),
        )
            .bind("activityId", activityId)
            .bind("order", order)
            .mapTo<Boolean>()
            .one()

    // Maneuver methods
    override fun storeManeuver(
        waveId: Int,
        waterManeuverId: Int,
        success: Boolean,
        order: Int,
    ): Int =
        handle.createUpdate(
            """
            insert into waveCoach.maneuver (wave, maneuver, success, maneuver_order) 
            values (:waveId, :waterManeuverId, :success, :order)
            """.trimIndent(),
        )
            .bind("waveId", waveId)
            .bind("waterManeuverId", waterManeuverId)
            .bind("success", success)
            .bind("order", order)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .one()

    override fun storeManeuvers(maneuvers: List<ManeuverToInsert>): List<Int> =
        handle.prepareBatch(
            """
            insert into waveCoach.maneuver (wave, maneuver, success, maneuver_order) 
            values (:waveId, :waterManeuverId, :success, :order)
            """
        ).use { batch ->
            maneuvers.forEach {
                batch.bind("waveId", it.waveId)
                    .bind("waterManeuverId", it.waterManeuverId)
                    .bind("success", it.success)
                    .bind("order", it.order)
                    .add()
            }
            batch.executeAndReturnGeneratedKeys()
                .mapTo<Int>()
                .list()
        }

    override fun getManeuversByWave(waveId: Int): List<Maneuver> =
        handle.createQuery(
            """
            select m.id, m.wave, wm.id as water_maneuver_id, wm.name as water_maneuver_name, 
                   wm.url, m.success, m.maneuver_order
            from waveCoach.maneuver m
            join waveCoach.water_maneuver wm on m.maneuver = wm.id
            where m.wave = :waveId
            order by m.maneuver_order
        """.trimIndent()
        )
            .bind("waveId", waveId)
            .mapTo<Maneuver>()
            .list()

    override fun getManeuverById(maneuverId: Int): Maneuver? =
        handle.createQuery(
            """
            select m.id, m.wave, wm.id as water_maneuver_id, wm.name as water_maneuver_name, 
                   wm.url, m.success, m.maneuver_order
            from waveCoach.maneuver m
            join waveCoach.water_maneuver wm on m.maneuver = wm.id
            where m.id = :maneuverId
        """.trimIndent()
        )
            .bind("maneuverId", maneuverId)
            .mapTo<Maneuver>()
            .singleOrNull()

    override fun updateManeuvers(maneuvers: List<ManeuverToUpdate>) {
        handle.prepareBatch(
            """
            update waveCoach.maneuver 
            set 
                success = coalesce(:success, success),
                maneuver_order = coalesce(:order, maneuver_order)
            where id = :id
            """.trimIndent(),
        ).use { batch ->
            maneuvers.forEach { maneuver ->
                batch.bind("id", maneuver.id)
                    .bind("success", maneuver.success)
                    .bind("order", maneuver.order)
                    .add()
            }
            batch.execute()
        }
    }

    override fun removeManeuversByActivity(activityId: Int) {
        handle.createUpdate(
            """
            delete from waveCoach.maneuver 
            where wave in (select id from waveCoach.wave where activity = :activityId)
            """.trimIndent(),
        )
            .bind("activityId", activityId)
            .execute()
    }

    override fun removeManeuversByAthlete(athleteId: Int) {
        handle.createUpdate(
            """
            delete from waveCoach.maneuver 
            where wave in (select id from waveCoach.wave where activity in (
                select id from waveCoach.activity where uid = :athleteId
            ))
            """.trimIndent(),
        )
            .bind("athleteId", athleteId)
            .execute()
    }

    override fun removeManeuversById(maneuverIds: List<Int>) {
        handle.prepareBatch(
            """
            delete from waveCoach.maneuver where id = :maneuverId
            """.trimIndent(),
        ).use { batch ->
            maneuverIds.forEach { maneuverId ->
                batch.bind("maneuverId", maneuverId).add()
            }
            batch.execute()
        }
    }

    override fun removeManeuverById(maneuverId: Int) {
        handle.createUpdate("delete from waveCoach.maneuver where id = :maneuverId")
            .bind("maneuverId", maneuverId)
            .execute()
    }

    override fun verifyManeuverOrder(waveId: Int, order: Int): Boolean =
        handle.createQuery(
            """
            select exists (select 1 from waveCoach.maneuver 
                where wave = :waveId and maneuver_order = :order
            )
            """.trimIndent(),
        )
            .bind("waveId", waveId)
            .bind("order", order)
            .mapTo<Boolean>()
            .one()

    private data class Row(
        val activityId: Int,
        val uid: Int,
        val microcycle: Int,
        val date: Long,
        val rpe: Int,
        val condition: String,
        val trimp: Int,
        val duration: Int,
        val waveId: Int?,
        val points: Float?,
        val rightSide: Boolean?,
        val waveOrder: Int?,
        val maneuverId: Int?,
        val waterManeuverId: Int?,
        val waterManeuverName: String?,
        val url: String?,
        val success: Boolean?,
        val maneuverOrder: Int?,
    )


    // Questionnaire methods
    override fun storeQuestionnaire(
        activityId: Int,
        sleep: Int,
        fatigue: Int,
        stress: Int,
        musclePain: Int,
    ) {
        handle.createUpdate(
            """
            insert into waveCoach.questionnaire (activity, sleep, fatigue, stress, muscle_pain) 
            values (:activityId, :sleep, :fatigue, :stress, :musclePain)
            """.trimIndent(),
        )
            .bind("activityId", activityId)
            .bind("sleep", sleep)
            .bind("fatigue", fatigue)
            .bind("stress", stress)
            .bind("musclePain", musclePain)
            .execute()
    }

    override fun getQuestionnaire(activityId: Int): Questionnaire? =
        handle.createQuery(" select * from waveCoach.questionnaire where activity = :activityId")
            .bind("activityId", activityId)
            .mapTo<Questionnaire>()
            .singleOrNull()

    override fun removeQuestionnaire(activityId: Int) {
        handle.createUpdate("delete from waveCoach.questionnaire where activity = :activityId")
            .bind("activityId", activityId)
            .execute()
    }
}

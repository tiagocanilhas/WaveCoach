package waveCoach.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import waveCoach.domain.Activity
import waveCoach.domain.ActivityType
import waveCoach.domain.Mesocycle
import waveCoach.domain.Microcycle
import waveCoach.repository.ActivityRepository

class JdbiActivityRepository(
    private val handle: Handle,
) : ActivityRepository {
    override fun storeActivity(uid: Int, date: Long, microcycle: Int): Int =
        handle.createUpdate("insert into waveCoach.activity (uid, date, microcycle) values (:uid, :date, :microcycle)")
            .bind("uid", uid)
            .bind("date", date)
            .bind("microcycle", microcycle)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .one()

    override fun storeMesocycle(
        uid: Int,
        startTime: Long,
        endTime: Long,
    ): Int =
        handle.createUpdate("""
            insert into waveCoach.mesocycle (uid, start_time, end_time) values (:uid, :start_time, :end_time)
        """.trimIndent())
            .bind("uid", uid)
            .bind("start_time", startTime)
            .bind("end_time", endTime)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .one()

    override fun storeMicrocycle(
        mesocycle: Int,
        startTime: Long,
        endTime: Long,
    ): Int =
        handle.createUpdate("""
            insert into waveCoach.microcycle (mesocycle, start_time, end_time) values (:mesocycle, :start_time, :end_time)
        """.trimIndent())
            .bind("mesocycle", mesocycle)
            .bind("start_time", startTime)
            .bind("end_time", endTime)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .one()

    override fun getMesocycle(id: Int): Mesocycle? =
        handle.createQuery("select * from waveCoach.mesocycle where id = :id")
            .bind("id", id)
            .mapTo<Mesocycle>()
            .singleOrNull()

    override fun getMicrocycle(id: Int): Microcycle? =
        handle.createQuery("select * from waveCoach.microcycle where id = :id")
            .bind("id", id)
            .mapTo<Microcycle>()
            .singleOrNull()

    override fun getMicrocycleByDate(
        date: Long,
        uid: Int,
    ): Microcycle? =
        handle.createQuery("""
            select * from waveCoach.microcycle 
            where start_time <= :date and end_time >= :date
        """.trimIndent())
            .bind("date", date)
            .bind("uid", uid)
            .mapTo<Microcycle>()
            .singleOrNull()

    override fun updateMesocycle(
        id: Int,
        startTime: Long,
        endTime: Long,
    ): Int =
        handle.createUpdate("""
            update waveCoach.mesocycle set start_time = :start_time, end_time = :end_time where id = :id
        """.trimIndent())
            .bind("id", id)
            .bind("start_time", startTime)
            .bind("end_time", endTime)
            .execute()

    override fun updateMicrocycle(
        id: Int,
        startTime: Long,
        endTime: Long,
    ): Int =
        handle.createUpdate("""
            update waveCoach.microcycle set start_time = :start_time, end_time = :end_time where id = :id
        """.trimIndent())
            .bind("id", id)
            .bind("start_time", startTime)
            .bind("end_time", endTime)
            .execute()

    private data class Row(
        val mesoId: Int,
        val uid: Int,
        val mesoStart: Long,
        val mesoEnd: Long,
        val microId: Int?,
        val microStart: Long?,
        val microEnd: Long?,
        val activityId: Int?,
        val activityDate: Long?,
        val activityType: ActivityType?
    )

    override fun getCalendar(
        uid: Int,
        type: ActivityType?,
    ): List<Mesocycle> {
        val query = """
            select 
                m.id as meso_id, m.uid as uid, m.start_time as meso_start, m.end_time as meso_end,
                mi.id as micro_id, mi.start_time as micro_start, mi.end_time as micro_end,
                a.id as activity_id, a.date as activity_date, a.type as activity_type
            from waveCoach.mesocycle m
            left join waveCoach.microcycle mi on mi.mesocycle = m.id
            left join waveCoach.activity a on a.microcycle = mi.id
            where m.uid = :uid and (:type is null or a.type = :type)
            order by m.start_time, mi.start_time, a.date
        """.trimIndent()

        val rows = handle.createQuery(query)
            .bind("uid", uid)
            .bind("type", type?.toString())
            .mapTo<Row>()

        return rows.groupBy { it.mesoId }
            .map { (mesoId, mesoRows) ->
                val mesoInfo = mesoRows.first()
                Mesocycle(
                    id = mesoId,
                    uid = mesoInfo.uid,
                    startTime = mesoInfo.mesoStart,
                    endTime = mesoInfo.mesoEnd,
                    microcycles =  mesoRows
                        .filter { it.microId != null }
                        .groupBy { it.microId }
                        .map { (microId, microRows) ->
                            val microInfo = microRows.first()
                            Microcycle(
                                id = microId!!,
                                mesocycle = mesoId,
                                startTime = microInfo.microStart!!,
                                endTime = microInfo.microEnd!!,
                                activities = microRows
                                    .filter { it.activityId != null }
                                    .map {
                                        Activity(
                                            id = it.activityId!!,
                                            uid = uid,
                                            microcycle = microId,
                                            date = it.activityDate!!,
                                            type = it.activityType
                                        )
                                    }
                            )
                    }
                )
            }
    }

    override fun getAthleteActivityList(uid: Int): List<Activity> =
        handle.createQuery("select * from waveCoach.activity where uid = :uid")
            .bind("uid", uid)
            .mapTo<Activity>()
            .list()

    override fun getActivity(uid: Int): Activity? =
        handle.createQuery("select * from waveCoach.activity where uid = :uid")
            .bind("uid", uid)
            .mapTo<Activity>()
            .singleOrNull()

    override fun getActivityById(activityId: Int): Activity? =
        handle.createQuery("select * from waveCoach.activity where id = :activityId")
            .bind("activityId", activityId)
            .mapTo<Activity>()
            .singleOrNull()

    override fun removeActivities(uid: Int) {
        handle.createUpdate("delete from waveCoach.activity where uid = :uid")
            .bind("uid", uid)
            .execute()
    }

    override fun removeActivity(activityId: Int) {
        handle.createUpdate("delete from waveCoach.activity where id = :activityId")
            .bind("activityId", activityId)
            .execute()
    }
}

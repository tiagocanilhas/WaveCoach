package waveCoach.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import waveCoach.domain.GymActivity
import waveCoach.repository.GymActivityRepository

class JdbiGymActivityRepository(
    private val handle: Handle,
) : GymActivityRepository {
    override fun storeGymActivity(activityId: Int): Int =
        handle.createUpdate("insert into waveCoach.gym (activity) values (:activity)")
            .bind("activity", activityId)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .one(
            )

    override fun getGymActivities(uid: Int): List<GymActivity> =
        handle.createQuery(
            """
        select * from waveCoach.gym where activity in (select id from waveCoach.activity where uid = :uid)
        """.trimIndent()
        )
            .bind("uid", uid)
            .mapTo<GymActivity>()
            .list()

    override fun removeGymActivities(uid: Int) {
        handle.createUpdate(
            """
        delete from waveCoach.gym where activity in (select id from waveCoach.activity where uid = :uid)
        """.trimIndent()
        )
            .bind("uid", uid)
            .execute()
    }
}
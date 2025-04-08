package waveCoach.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import waveCoach.domain.Activity
import waveCoach.repository.ActivityRepository

class JdbiActivityRepository(
    private val handle: Handle,
) : ActivityRepository {
    override fun storeActivity(uid: Int, date: Long): Int =
        handle.createUpdate("insert into waveCoach.activity (uid, date) values (:uid, :date)")
            .bind("uid", uid)
            .bind("date", date)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .one()

    override fun getAthleteActivityList(uid: Int): List<Activity> =
        handle.createQuery("select * from waveCoach.activity where uid = :uid")
            .bind("uid", uid)
            .mapTo<Activity>()
            .list()

    override fun removeActivities(uid: Int) {
        handle.createUpdate("delete from waveCoach.activity where uid = :uid")
            .bind("uid", uid)
            .execute()
    }
}
package waveCoach.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import waveCoach.domain.Activity
import waveCoach.repository.ActivityRepository

class JdbiActivityRepository(
    private val handle: Handle,
) : ActivityRepository {
    override fun storeActivity(uid: Int, date: Long, type: String): Int =
        handle.createUpdate("insert into waveCoach.activity (uid, date, type) values (:uid, :date, :type)")
            .bind("uid", uid)
            .bind("date", date)
            .bind("type", type)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .one()

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

package waveCoach.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
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
}
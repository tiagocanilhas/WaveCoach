package waveCoach.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import waveCoach.repository.CoachRepository

class JdbiCoachRepository(
    private val handle: Handle,
) : CoachRepository {
    override fun storeCoach(uid: Int): Int =
        handle.createUpdate("insert into waveCoach.coach (uid) values (:uid)")
            .bind("uid", uid)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .one()
}

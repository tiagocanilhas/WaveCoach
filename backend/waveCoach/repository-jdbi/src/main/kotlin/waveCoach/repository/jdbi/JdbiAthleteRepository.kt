package waveCoach.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import waveCoach.repository.AthleteRepository

class JdbiAthleteRepository(
    private val handle: Handle,
): AthleteRepository {
    override fun storeAthlete(uid: Int, coachId: Int, name: String, birthDate: String): Int =
        handle.createUpdate("insert into waveCoach.athlete (uid, coach_id, name, birth_date) values (:uid, :coach, :name, :birth_date)")
            .bind("uid", uid)
            .bind("coach", coachId)
            .bind("name", name)
            .bind("birth_date", birthDate)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .one()
}
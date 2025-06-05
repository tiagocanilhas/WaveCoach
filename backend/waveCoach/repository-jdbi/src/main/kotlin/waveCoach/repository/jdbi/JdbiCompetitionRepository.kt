package waveCoach.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import waveCoach.repository.CompetitionRepository

class JdbiCompetitionRepository(
    private val handle: Handle,
): CompetitionRepository {
    override fun storeCompetition(
        uid: Int,
        date: Long,
        location: String
    ): Int {
        return handle.createUpdate(
            """
            insert into waveCoach.competition (uid, date, location) 
            values (:uid, :date, :location)
            """.trimIndent(),
        )
            .bind("uid", uid)
            .bind("date", date)
            .bind("location", location)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .one()
    }

    override fun competitionExists(
        id: Int
    ): Boolean {
        return handle.createQuery(
            """
            select exists(select 1 from waveCoach.competition where id = :id)
            """.trimIndent(),
        )
            .bind("id", id)
            .mapTo<Boolean>()
            .one()
    }
}
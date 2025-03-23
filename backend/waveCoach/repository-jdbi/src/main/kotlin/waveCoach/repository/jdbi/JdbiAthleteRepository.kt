package waveCoach.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import waveCoach.domain.Athlete
import waveCoach.repository.AthleteRepository

class JdbiAthleteRepository(
    private val handle: Handle,
) : AthleteRepository {
    override fun storeAthlete(uid: Int, coachId: Int, name: String, birthDate: Long): Int =
        handle.createUpdate(
            """
            insert into waveCoach.athlete (uid, coach, name, birth_date) 
            values (:uid, :coach, :name, :birth_date)
        """.trimIndent()
        )
            .bind("uid", uid)
            .bind("coach", coachId)
            .bind("name", name)
            .bind("birth_date", birthDate)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .one()

    override fun getAthlete(uid: Int): Athlete? =
        handle.createQuery("select * from waveCoach.athlete where uid = :uid")
            .bind("uid", uid)
            .mapTo<Athlete>()
            .singleOrNull()

    override fun getAthleteList(coachId: Int): List<Athlete> =
        handle.createQuery("select * from waveCoach.athlete where coach = :coach")
            .bind("coach", coachId)
            .mapTo<Athlete>()
            .list()

    override fun updateAthlete(uid: Int, name: String, birthDate: Long) {
        handle.createUpdate(
            """
            update waveCoach.athlete 
            set name = :name, birth_date = :birth_date 
            where uid = :uid
        """.trimIndent()
        )
            .bind("uid", uid)
            .bind("name", name)
            .bind("birth_date", birthDate)
            .execute()
    }

    override fun removeAthlete(uid: Int) {
        handle.createUpdate("delete from waveCoach.athlete where uid = :uid")
            .bind("uid", uid)
            .execute()
    }
}
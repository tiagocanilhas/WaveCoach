package waveCoach.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import waveCoach.domain.Athlete
import waveCoach.domain.AthleteCode
import waveCoach.domain.CodeValidationInfo
import waveCoach.repository.AthleteRepository

class JdbiAthleteRepository(
    private val handle: Handle,
) : AthleteRepository {
    override fun storeAthlete(
        uid: Int,
        coachId: Int,
        name: String,
        birthDate: Long,
        url: String?,
    ): Int =
        handle.createUpdate(
            """
            insert into waveCoach.athlete (uid, coach, name, birthdate, url)
            values (:uid, :coach, :name, :birthdate, :url)
            """.trimIndent(),
        )
            .bind("uid", uid)
            .bind("coach", coachId)
            .bind("name", name)
            .bind("birthdate", birthDate)
            .bind("url", url)
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

    override fun updateAthlete(
        uid: Int,
        name: String,
        birthDate: Long,
    ) {
        handle.createUpdate(
            """
            update waveCoach.athlete 
            set name = :name, birthdate = :birthdate 
            where uid = :uid
            """.trimIndent(),
        )
            .bind("uid", uid)
            .bind("name", name)
            .bind("birthdate", birthDate)
            .execute()
    }

    override fun setCredentialsChangedToTrue(uid: Int) {
        handle.createUpdate("update waveCoach.athlete set credentials_changed = true where uid = :uid")
            .bind("uid", uid)
            .execute()
    }

    override fun removeAthlete(uid: Int) {
        handle.createUpdate("delete from waveCoach.athlete where uid = :uid")
            .bind("uid", uid)
            .execute()
    }

    override fun storeCode(code: AthleteCode) {
        handle.createUpdate(
            """
            insert into waveCoach.code (uid, code, created_time)
            values (:uid, :code, :created_time)
            on conflict (uid) do update set
            code = :code, created_time = :created_time
            """.trimIndent(),
        )
            .bind("uid", code.uid)
            .bind("code", code.codeValidationInfo.value)
            .bind("created_time", code.createdTime.epochSeconds)
            .execute()
    }

    private data class AthleteUsernameAndCreatedTime(
        val uid: Int,
        val coach: Int,
        val name: String,
        val birthDate: Long,
        val credentialsChanged: Boolean,
        val url: String?,
        val username: String,
        val createdTime: Long,
    ) {
        val athleteUsernameAndCreatedTime: Triple<Athlete, String, Long>
            get() =
                Triple(
                    Athlete(uid, coach, name, birthDate, credentialsChanged, url),
                    username,
                    createdTime,
                )
    }

    override fun getByCode(code: CodeValidationInfo): Triple<Athlete, String, Long>? =
        handle.createQuery(
            """
            select a.*, u.username, c.created_time from waveCoach.athlete a
            join waveCoach.code c on a.uid = c.uid 
            join waveCoach.user u on a.uid = u.id
            where c.code = :code
            """.trimIndent(),
        )
            .bind("code", code.value)
            .mapTo<AthleteUsernameAndCreatedTime>()
            .singleOrNull()
            ?.athleteUsernameAndCreatedTime

    override fun removeCode(uid: Int) {
        handle.createUpdate("delete from waveCoach.code where uid = :uid")
            .bind("uid", uid)
            .execute()
    }
}

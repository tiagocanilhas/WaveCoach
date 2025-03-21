package waveCoach.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import waveCoach.domain.Characteristics
import waveCoach.repository.CharacteristicsRepository


class JdbiCharacteristicsRepository(
    private val handle: Handle
) : CharacteristicsRepository {
    override fun storeCharacteristics(
        uid: Int,
        date: Long,
        height: Int?,
        weight: Float?,
        calories: Int?,
        waist: Int?,
        arm: Int?,
        thigh: Int?,
        tricep: Float?,
        abdominal: Float?
    ): Long =
        handle.createUpdate("""
            insert into waveCoach.characteristics (
                uid, date, height, weight, calories, waist, arm, thigh, tricep, abdominal
            ) values (
                :uid, :date, :height, :weight, :calories, :waist, :arm, :thigh, :tricep, :abdominal
            )
        """.trimIndent())
            .bind("uid", uid)
            .bind("date", date)
            .bind("height", height)
            .bind("weight", weight)
            .bind("calories", calories)
            .bind("waist", waist)
            .bind("arm", arm)
            .bind("thigh", thigh)
            .bind("tricep", tricep)
            .bind("abdominal", abdominal)
            .executeAndReturnGeneratedKeys()
            .mapTo<Long>()
            .one()

    override fun storeCharacteristicsWithoutDate(
        uid: Int,
        height: Int?,
        weight: Float?,
        calories: Int?,
        waist: Int?,
        arm: Int?,
        thigh: Int?,
        tricep: Float?,
        abdominal: Float?
    ): Long  =
        handle.createUpdate("""
            insert into waveCoach.characteristics 
                (uid, height, weight, calories, waist, arm, thigh, tricep, abdominal) 
            values (:uid, :height, :weight, :calories, :waist, :arm, :thigh, :tricep, :abdominal)
        """.trimIndent())
            .bind("uid", uid)
            .bind("height", height)
            .bind("weight", weight)
            .bind("calories", calories)
            .bind("waist", waist)
            .bind("arm", arm)
            .bind("thigh", thigh)
            .bind("tricep", tricep)
            .bind("abdominal", abdominal)
            .executeAndReturnGeneratedKeys()
            .mapTo<Long>()
            .one()

    override fun getCharacteristics(uid: Int, date: Long): Characteristics? =
        handle.createQuery("select * from waveCoach.characteristics where uid = :uid and date = :date")
            .bind("uid", uid)
            .bind("date", date)
            .mapTo<Characteristics>()
            .singleOrNull()

    override fun getCharacteristicsList(uid: Int): List<Characteristics> =
        handle.createQuery("select * from waveCoach.characteristics where uid = :uid")
            .bind("uid", uid)
            .mapTo<Characteristics>()
            .list()

    override fun updateCharacteristics(
        uid: Int,
        date: Long,
        height: Int?,
        weight: Float?,
        calories: Int?,
        waist: Int?,
        arm: Int?,
        thigh: Int?,
        tricep: Float?,
        abdominal: Float?
    ) {
        val query = """
            update waveCoach.characteristics set
                height = :height,
                weight = :weight,
                calories = :calories,
                waist = :waist,
                arm = :arm,
                thigh = :thigh,
                tricep = :tricep,
                abdominal = :abdominal
            where uid = :uid and date = :date
        """.trimIndent()
        handle.createUpdate(query)
            .bind("uid", uid)
            .bind("date", date)
            .bind("height", height)
            .bind("weight", weight)
            .bind("calories", calories)
            .bind("waist", waist)
            .bind("arm", arm)
            .bind("thigh", thigh)
            .bind("tricep", tricep)
            .bind("abdominal", abdominal)
            .execute()
    }

    override fun removeCharacteristics(uid: Int, date: Long) {
        val query = """
            delete from waveCoach.characteristics where uid = :uid and date = :date
        """.trimIndent()
        handle.createUpdate(query)
            .bind("uid", uid)
            .bind("date", date)
            .execute()
    }

    override fun removeCharacteristicsWithoutDate(uid: Int) {
        val query = """
            delete from waveCoach.characteristics where uid = :uid
        """.trimIndent()
        handle.createUpdate(query)
            .bind("uid", uid)
            .execute()
    }
}
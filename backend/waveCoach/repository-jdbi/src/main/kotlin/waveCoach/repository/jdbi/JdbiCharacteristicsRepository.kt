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
        date: Long?,
        height: Int?,
        weight: Float?,
        calories: Int?,
        waist: Int?,
        arm: Int?,
        thigh: Int?,
        tricep: Float?,
        abdominal: Float?
    ) {
        handle.createUpdate("""
            insert into waveCoach.characteristics 
                (uid, date, height, weight, calories, waist, arm, thigh, tricep, abdominal) 
            values (:uid, :date, :height, :weight, :calories, :waist, :arm, :thigh, :tricep, :abdominal)
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
            .execute()
    }

    override fun getCharacteristics(uid: Int, date: Long): Characteristics? =
        handle.createQuery("select * from waveCoach.characteristics where uid = :uid and date = :date")
            .bind("uid", uid)
            .bind("date", date)
            .mapTo<Characteristics>()
            .singleOrNull()
}
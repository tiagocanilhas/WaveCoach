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
        bodyFat: Float?,
        waistSize: Int?,
        armSize: Int?,
        thighSize: Int?,
        tricepFat: Int?,
        abdomenFat: Int?,
        thighFat: Int?
    ): Long =
        handle.createUpdate(
            """
            insert into waveCoach.characteristics 
                (uid, date, height, weight, calories, body_fat, waist_size, arm_size, thigh_size, tricep_fat, abdomen_fat, thigh_fat) 
            values (:uid, :date, :height, :weight, :calories, :body_fat, :waist_size, :arm_size, :thigh_size, :tricep_fat, :abdomen_fat, :thigh_fat)
        """.trimIndent()
        )
            .bind("uid", uid)
            .bind("date", date)
            .bind("height", height)
            .bind("weight", weight)
            .bind("calories", calories)
            .bind("body_fat", bodyFat)
            .bind("waist_size", waistSize)
            .bind("arm_size", armSize)
            .bind("thigh_size", thighSize)
            .bind("tricep_fat", tricepFat)
            .bind("abdomen_fat", abdomenFat)
            .bind("thigh_fat", thighFat)
            .executeAndReturnGeneratedKeys()
            .mapTo<Long>()
            .one()

    override fun storeCharacteristicsWithoutDate(
        uid: Int,
        height: Int?,
        weight: Float?,
        calories: Int?,
        bodyFat: Float?,
        waistSize: Int?,
        armSize: Int?,
        thighSize: Int?,
        tricepFat: Int?,
        abdomenFat: Int?,
        thighFat: Int?
    ): Long =
        handle.createUpdate(
            """
            insert into waveCoach.characteristics 
                (uid, height, weight, calories, body_fat, waist_size, arm_size, thigh_size, tricep_fat, abdomen_fat, thigh_fat) 
            values (:uid, :height, :weight, :calories, :body_fat, :waist_size, :arm_size, :thigh_size, :tricep_fat, :abdomen_fat, :thigh_fat)
        """.trimIndent()
        )
            .bind("uid", uid)
            .bind("height", height)
            .bind("weight", weight)
            .bind("calories", calories)
            .bind("body_fat", bodyFat)
            .bind("waist_size", waistSize)
            .bind("arm_size", armSize)
            .bind("thigh_size", thighSize)
            .bind("tricep_fat", tricepFat)
            .bind("abdomen_fat", abdomenFat)
            .bind("thigh_fat", thighFat)
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
        bodyFat: Float?,
        waistSize: Int?,
        armSize: Int?,
        thighSize: Int?,
        tricepFat: Int?,
        abdomenFat: Int?,
        thighFat: Int?
    ) {
        handle.createUpdate(
            """
            update waveCoach.characteristics set 
                height = :height, weight = :weight, calories = :calories, body_fat = :body_fat,
                waist_size = :waist_size, arm_size = :arm_size, thigh_size = :thigh_size, tricep_fat = :tricep_fat, 
                abdomen_fat = :abdomen_fat, thigh_fat = :thigh_fat
            where uid = :uid and date = :date
        """.trimIndent()
        )
            .bind("uid", uid)
            .bind("date", date)
            .bind("height", height)
            .bind("weight", weight)
            .bind("calories", calories)
            .bind("body_fat", bodyFat)
            .bind("waist_size", waistSize)
            .bind("arm_size", armSize)
            .bind("thigh_size", thighSize)
            .bind("tricep_fat", tricepFat)
            .bind("abdomen_fat", abdomenFat)
            .bind("thigh_fat", thighFat)
            .execute()
    }

    override fun removeCharacteristics(uid: Int, date: Long) {
        handle.createUpdate("delete from waveCoach.characteristics where uid = :uid and date = :date")
            .bind("uid", uid)
            .bind("date", date)
            .execute()
    }

    override fun removeCharacteristicsWithoutDate(uid: Int) {
        handle.createUpdate("delete from waveCoach.characteristics where uid = :uid")
            .bind("uid", uid)
            .execute()
    }
}
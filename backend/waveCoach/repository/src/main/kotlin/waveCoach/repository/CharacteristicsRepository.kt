package waveCoach.repository

import waveCoach.domain.Characteristics

interface CharacteristicsRepository {
    fun storeCharacteristics(
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
    ): Long

    fun storeCharacteristicsWithoutDate(
        uid: Int,
        height: Int?,
        weight: Float?,
        calories: Int?,
        waist: Int?,
        arm: Int?,
        thigh: Int?,
        tricep: Float?,
        abdominal: Float?
    ): Long

    fun getCharacteristics(uid: Int, date: Long): Characteristics?

    fun getCharacteristicsList(uid: Int): List<Characteristics>

    fun updateCharacteristics(
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
    )

    fun removeCharacteristics(uid: Int, date: Long)

    fun removeCharacteristicsWithoutDate(uid: Int)
}
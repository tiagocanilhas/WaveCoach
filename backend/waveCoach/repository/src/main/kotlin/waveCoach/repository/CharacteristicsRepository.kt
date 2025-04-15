package waveCoach.repository

import waveCoach.domain.Characteristics

interface CharacteristicsRepository {
    fun storeCharacteristics(
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
        thighFat: Int?,
    ): Long

    fun storeCharacteristicsWithoutDate(
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
        thighFat: Int?,
    ): Long

    fun getCharacteristics(
        uid: Int,
        date: Long,
    ): Characteristics?

    fun getCharacteristicsList(uid: Int): List<Characteristics>

    fun updateCharacteristics(
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
        thighFat: Int?,
    )

    fun removeCharacteristics(
        uid: Int,
        date: Long,
    )

    fun removeCharacteristicsWithoutDate(uid: Int)
}

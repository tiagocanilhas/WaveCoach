package waveCoach.repository

import waveCoach.domain.Characteristics

interface CharacteristicsRepository {
    fun storeCharacteristics(
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
    )

    fun getCharacteristics(uid: Int, date: Long): Characteristics?
}
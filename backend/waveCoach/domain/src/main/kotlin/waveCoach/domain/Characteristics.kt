package waveCoach.domain

data class Characteristics(
    val uid: Int,
    val date: Long,
    val height: Int?,
    val weight: Float?,
    val calories: Int?,
    val waist: Int?,
    val arm: Int?,
    val thigh: Int?,
    val tricep: Float?,
    val abdominal: Float?
)
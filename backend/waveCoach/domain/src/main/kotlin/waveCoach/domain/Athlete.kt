package waveCoach.domain

data class Athlete (
    val uid: Int,
    val coach: Int,
    val name: String,
    val birthDate: Long
)
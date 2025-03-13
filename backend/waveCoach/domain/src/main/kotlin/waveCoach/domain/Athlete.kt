package waveCoach.domain

data class Athlete (
    val id: Int,
    val coachId: Int,
    val name: String,
    val birthDate: String
)
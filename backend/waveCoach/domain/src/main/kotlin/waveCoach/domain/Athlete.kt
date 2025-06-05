package waveCoach.domain

data class Athlete(
    val uid: Int,
    val coach: Int,
    val name: String,
    val birthdate: Long,
    val credentialsChanged: Boolean,
    val url: String?,
)

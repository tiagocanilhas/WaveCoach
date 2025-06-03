package waveCoach.domain

data class AuthenticatedCoach(
    val info: User,
    val token: String,
)

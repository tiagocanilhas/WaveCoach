package waveCoach.domain

class AuthenticatedUser(
    val info: User,
    val token: String,
)
package waveCoach.domain

data class User(
    val id: Int,
    val username: String,
    val password: PasswordValidationInfo,
)
package pt.isel.daw.imSystem.domain

data class User(
    val id: Int,
    val username: String,
    val password: PasswordValidationInfo,
)
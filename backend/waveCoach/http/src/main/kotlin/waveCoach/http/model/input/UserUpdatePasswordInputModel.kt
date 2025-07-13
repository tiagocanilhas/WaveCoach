package waveCoach.http.model.input

data class UserUpdatePasswordInputModel(
    val oldPassword: String,
    val newPassword: String,
)

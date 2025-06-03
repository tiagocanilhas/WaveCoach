package waveCoach.http.model.output

data class LoginOutputModel(
    val id: Int,
    val username: String,
    val token: String,
    val isCoach: Boolean,
)

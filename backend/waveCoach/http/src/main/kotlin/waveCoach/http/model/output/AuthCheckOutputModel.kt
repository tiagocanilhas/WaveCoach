package waveCoach.http.model.output

data class AuthCheckOutputModel(
    val id: Int,
    val username: String,
    val isCoach: Boolean
)
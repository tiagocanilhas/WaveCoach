package waveCoach.http.model.input

data class AthleteChangeCredentialsInputModel(
    val code: String,
    val username: String,
    val password: String,
)

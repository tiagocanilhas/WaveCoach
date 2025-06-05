package waveCoach.http.model.output

data class AthleteOutputModel(
    val uid: Int,
    val coach: Int,
    val name: String,
    val birthdate: Long,
    val credentialsChanged: Boolean,
    val url: String?,
)

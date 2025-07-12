package waveCoach.http.model.output

data class ManeuverOutputModel(
    val id: Int,
    val waterManeuverId: Int,
    val name: String,
    val url: String?,
    val success: Boolean,
    val order: Int,
)

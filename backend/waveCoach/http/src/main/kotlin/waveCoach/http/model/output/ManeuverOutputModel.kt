package waveCoach.http.model.output

data class ManeuverOutputModel(
    val id: Int,
    val waterManeuverId: Int,
    val waterManeuverName: String,
    val url: String?,
    val rightSide: Boolean,
    val success: Boolean,
)
package waveCoach.http.model.input

data class UpdateManeuverInputModel(
    val id: Int?,
    val waterManeuverId: Int?,
    val success: Boolean?,
    val order: Int?,
)
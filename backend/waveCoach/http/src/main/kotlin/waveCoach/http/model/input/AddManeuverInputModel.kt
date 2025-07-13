package waveCoach.http.model.input

data class AddManeuverInputModel(
    val waterManeuverId: Int,
    val success: Boolean,
    val order: Int,
)

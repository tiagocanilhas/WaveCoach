package waveCoach.http.model.input

data class ManeuverInputModel(
    val waterManeuverId: Int,
    val rightSide: Boolean,
    val success : Boolean,
)
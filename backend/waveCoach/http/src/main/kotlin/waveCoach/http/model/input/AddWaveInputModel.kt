package waveCoach.http.model.input

data class AddWaveInputModel(
    val points: Float?,
    val rightSide: Boolean,
    val maneuvers: List<ManeuverInputModel>,
    val order: Int
)
package waveCoach.http.model.input

data class WaveInputModel(
    val points: Float?,
    val rightSide: Boolean,
    val maneuvers: List<ManeuverInputModel>,
)

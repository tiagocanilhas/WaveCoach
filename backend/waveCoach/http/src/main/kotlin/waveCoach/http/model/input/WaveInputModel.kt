package waveCoach.http.model.input

data class WaveInputModel(
    val points: Float?,
    val maneuvers: List<ManeuverInputModel>,
)

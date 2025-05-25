package waveCoach.http.model.output

data class WaveOutputModel(
    val id: Int,
    val points: Float?,
    val maneuvers: List<ManeuverOutputModel>,
)
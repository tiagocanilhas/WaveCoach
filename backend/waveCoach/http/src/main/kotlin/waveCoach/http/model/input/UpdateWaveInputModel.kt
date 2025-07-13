package waveCoach.http.model.input

data class UpdateWaveInputModel(
    val id: Int?,
    val points: Float?,
    val rightSide: Boolean?,
    val maneuvers: List<UpdateManeuverInputModel>?,
    val order: Int?,
)

package waveCoach.http.model.input

data class UpdateWaveInputModel(
    val id: Int?,
    val points: Float?,
    val rightSide: Boolean?,
    val order: Int?,
    val maneuvers: List<UpdateManeuverInputModel>?,
)
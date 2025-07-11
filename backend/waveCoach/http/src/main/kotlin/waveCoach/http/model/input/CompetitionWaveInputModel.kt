package waveCoach.http.model.input

data class CompetitionWaveInputModel (
    val points: Float?,
    val rightSide: Boolean,
    val maneuvers: List<CompetitionManeuverInputModel>,
)
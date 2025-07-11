package waveCoach.http.model.input

data class WaterActivityInputModel(
    val athleteId: Int,
    val rpe: Int,
    val condition: String,
    val trimp: Int,
    val duration: Int,
    val waves: List<CompetitionWaveInputModel>,
)
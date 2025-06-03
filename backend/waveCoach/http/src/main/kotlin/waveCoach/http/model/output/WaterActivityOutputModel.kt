package waveCoach.http.model.output

data class WaterActivityOutputModel(
    val id: Int,
    val athleteId: Int,
    val microcycleId: Int,
    val date: Long,
    val rpe: Int,
    val condition: String,
    val trimp: Int,
    val duration: Int,
    val waves: List<WaveOutputModel>,
)

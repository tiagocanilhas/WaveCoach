package waveCoach.http.model.output

data class WaterActivityOutputModel(
    val id: Int,
    val athleteId: Int,
    val microcycleId: Int,
    val date: Long,
    val pse: Int,
    val condition: String,
    val heartRate: Int,
    val duration: Int,
    val waves: List<WaveOutputModel>,
)


package waveCoach.http.model.output

data class MicrocycleWaterOutputModel(
    val id: Int,
    val startTime: Long,
    val endTime: Long,
    val activities: List<WaterActivityOutputModel>,
)

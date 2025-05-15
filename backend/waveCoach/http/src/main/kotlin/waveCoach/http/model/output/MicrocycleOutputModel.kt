package waveCoach.http.model.output

data class MicrocycleOutputModel (
    val id: Int,
    val startTime: Long,
    val endTime: Long,
    val activities: List<ActivityOutputModel>,
)
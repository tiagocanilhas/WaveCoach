package waveCoach.domain

data class Microcycle(
    val id: Int,
    val mesocycle: Int,
    val startTime: Long,
    val endTime: Long,
    val activities: List<Activity> = emptyList(),
)

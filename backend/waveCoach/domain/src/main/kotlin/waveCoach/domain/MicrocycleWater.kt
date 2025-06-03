package waveCoach.domain

data class MicrocycleWater(
    val id: Int,
    val startTime: Long,
    val endTime: Long,
    val activities: List<WaterActivityWithWaves> = emptyList(),
)

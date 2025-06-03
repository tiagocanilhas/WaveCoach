package waveCoach.domain

data class MesocycleWater(
    val id: Int,
    val uid: Int,
    val startTime: Long,
    val endTime: Long,
    val microcycles: List<MicrocycleWater> = emptyList(),
)

package waveCoach.domain

data class Mesocycle(
    val id: Int,
    val uid: Int,
    val startTime: Long,
    val endTime: Long,
    val microcycles: List<Microcycle> = emptyList(),
)
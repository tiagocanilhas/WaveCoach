package waveCoach.domain

data class Sets(
    val id: Int,
    val exerciseId: Int,
    val reps: Int,
    val weight: Float,
    val restTime: Float,
    val setOrder: Int,
)

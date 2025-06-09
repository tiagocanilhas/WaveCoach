package waveCoach.domain

data class SetToInsert(
    val exerciseId: Int,
    val reps: Int,
    val weight: Float,
    val restTime: Float,
    val setOrder: Int,
)
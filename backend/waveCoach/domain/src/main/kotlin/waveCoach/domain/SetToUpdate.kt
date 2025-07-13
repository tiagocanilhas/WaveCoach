package waveCoach.domain

data class SetToUpdate(
    val id: Int,
    val reps: Int?,
    val weight: Float?,
    val restTime: Float?,
    val order: Int?,
)

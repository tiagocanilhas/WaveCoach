package waveCoach.domain

data class WaveToInsert(
    val activityId: Int,
    val points: Float?,
    val rightSide: Boolean,
    val order: Int,
)

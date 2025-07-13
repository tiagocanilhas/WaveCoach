package waveCoach.domain

data class WaveToUpdate(
    val id: Int,
    val points: Float?,
    val rightSide: Boolean?,
    val order: Int?,
)

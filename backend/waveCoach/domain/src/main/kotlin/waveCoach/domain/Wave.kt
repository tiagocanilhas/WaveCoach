package waveCoach.domain

data class Wave(
    val id: Int,
    val activity: Int,
    val points: Float?,
    val rightSide: Boolean,
    val waveOrder: Int,
)

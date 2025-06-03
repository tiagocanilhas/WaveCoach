package waveCoach.domain

data class Exercise(
    val id: Int,
    val activity: Int,
    val name: String,
    val exerciseOrder: Int,
    val url: String?,
)

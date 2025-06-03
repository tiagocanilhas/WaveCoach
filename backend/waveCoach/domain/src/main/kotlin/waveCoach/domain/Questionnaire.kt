package waveCoach.domain

data class Questionnaire(
    val id: Int,
    val activity: Int,
    val sleep: Int,
    val fatigue: Int,
    val stress: Int,
    val musclePain: Int,
)

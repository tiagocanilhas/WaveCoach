package waveCoach.http.model.input

data class QuestionnaireCreateInputModel(
    val sleep: Int,
    val fatigue: Int,
    val stress: Int,
    val musclePain: Int,
)

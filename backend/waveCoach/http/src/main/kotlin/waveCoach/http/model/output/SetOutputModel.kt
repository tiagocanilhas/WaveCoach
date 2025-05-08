package waveCoach.http.model.output

data class SetOutputModel(
    val setId: Int,
    val reps: Int,
    val weight: Float,
    val restTime: Float,
    val setOrder: Int,
)
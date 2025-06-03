package waveCoach.http.model.output

data class SetOutputModel(
    val id: Int,
    val reps: Int,
    val weight: Float,
    val restTime: Float,
    val order: Int,
)

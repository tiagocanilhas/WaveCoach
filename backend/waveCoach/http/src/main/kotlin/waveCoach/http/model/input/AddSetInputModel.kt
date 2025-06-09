package waveCoach.http.model.input

data class AddSetInputModel(
    val reps: Int,
    val weight: Float,
    val restTime: Float,
    val order: Int,
)

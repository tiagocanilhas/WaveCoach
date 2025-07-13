package waveCoach.http.model.input

data class AddExerciseInputModel(
    val sets: List<SetInputModel>,
    val gymExerciseId: Int,
    val order: Int,
)

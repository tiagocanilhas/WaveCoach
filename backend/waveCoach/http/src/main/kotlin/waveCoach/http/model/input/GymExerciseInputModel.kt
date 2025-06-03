package waveCoach.http.model.input

data class GymExerciseInputModel(
    val sets: List<SetInputModel>,
    val gymExerciseId: Int,
)

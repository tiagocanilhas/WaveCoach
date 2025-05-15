package waveCoach.http.model.output

data class ExerciseWithSetsOutputModel(
    val id: Int,
    val activity: Int,
    val gymExercise: String,
    val order: Int,
    val sets: List<SetOutputModel>,
)
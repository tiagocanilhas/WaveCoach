package waveCoach.http.model.output

data class ExerciseWithSetsOutputModel(
    val exerciseId: Int,
    val activity: Int,
    val gymExerciseId: Int,
    val exerciseOrder: Int,
    val sets: List<SetOutputModel>,
)
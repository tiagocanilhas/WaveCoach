package waveCoach.http.model.output

data class ActivityWithExercisesOutputModel(
    val id: Int,
    val uid: Int,
    val date: Long,
    val type: String,
    val exercises: List<ExerciseWithSetsOutputModel>,
)
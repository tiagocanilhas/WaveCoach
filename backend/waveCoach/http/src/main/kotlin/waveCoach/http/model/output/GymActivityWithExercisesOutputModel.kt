package waveCoach.http.model.output

data class GymActivityWithExercisesOutputModel(
    val id: Int,
    val date: Long,
    val exercises: List<ExerciseWithSetsOutputModel>,
)

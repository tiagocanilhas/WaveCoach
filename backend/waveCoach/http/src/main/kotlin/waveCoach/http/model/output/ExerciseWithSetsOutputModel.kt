package waveCoach.http.model.output

data class ExerciseWithSetsOutputModel(
    val id: Int,
    val activity: Int,
    val name: String,
    val order: Int,
    val sets: List<SetOutputModel>,
)
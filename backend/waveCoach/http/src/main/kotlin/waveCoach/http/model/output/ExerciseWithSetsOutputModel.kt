package waveCoach.http.model.output

data class ExerciseWithSetsOutputModel(
    val id: Int,
    val name: String,
    val order: Int,
    val url: String?,
    val sets: List<SetOutputModel>,
)
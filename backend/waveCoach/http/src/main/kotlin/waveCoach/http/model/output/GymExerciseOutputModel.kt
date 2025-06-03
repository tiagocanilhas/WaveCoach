package waveCoach.http.model.output

data class GymExerciseOutputModel(
    val id: Int,
    val name: String,
    val category: String,
    val url: String?,
)

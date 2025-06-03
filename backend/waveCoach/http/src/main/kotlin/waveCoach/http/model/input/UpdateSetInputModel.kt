package waveCoach.http.model.input

data class UpdateSetInputModel(
    val id: Int?,
    val reps: Int?,
    val weight: Float?,
    val rest: Float?,
    val setOrder: Int?,
)

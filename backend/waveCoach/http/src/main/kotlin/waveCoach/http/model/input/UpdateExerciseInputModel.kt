package waveCoach.http.model.input

data class UpdateExerciseInputModel(
    val id: Int?,
    val sets: List<UpdateSetInputModel>?,
    val gymExerciseId: Int?,
    val order: Int?,
)

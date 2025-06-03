package waveCoach.http.model.input

data class UpdateGymActivityInputModel(
    val date: String?,
    val exercises: List<UpdateExerciseInputModel>?,
)

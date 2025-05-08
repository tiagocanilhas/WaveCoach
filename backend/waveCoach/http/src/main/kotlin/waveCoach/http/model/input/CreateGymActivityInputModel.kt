package waveCoach.http.model.input

data class CreateGymActivityInputModel(
    val date: String,
    val exercises: List<GymExerciseInputModel>,
)

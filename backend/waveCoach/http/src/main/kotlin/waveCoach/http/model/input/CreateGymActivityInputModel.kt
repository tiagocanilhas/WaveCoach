package waveCoach.http.model.input

data class CreateGymActivityInputModel(
    val athleteId: Int,
    val date: String,
    val exercises: List<GymExerciseInputModel>,
)

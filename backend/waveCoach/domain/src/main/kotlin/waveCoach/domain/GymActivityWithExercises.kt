package waveCoach.domain

data class GymActivityWithExercises(
    val id: Int,
    val uid: Int,
    val date: Long,
    val type: ActivityType?,
    val exercises: List<ExerciseWithSets>,
)

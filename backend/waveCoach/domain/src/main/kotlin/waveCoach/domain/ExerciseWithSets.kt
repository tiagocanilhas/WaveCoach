package waveCoach.domain

data class ExerciseWithSets(
    val id: Int,
    val activity: Int,
    val gymExercise: String,
    val exerciseOrder: Int,
    val sets: List<Sets>,
)
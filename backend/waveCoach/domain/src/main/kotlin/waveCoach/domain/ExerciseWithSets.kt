package waveCoach.domain

data class ExerciseWithSets(
    val id: Int,
    val activity: Int,
    val gymExercise: Int,
    val name: String,
    val exerciseOrder: Int,
    val url: String?,
    val sets: List<Sets>,
)

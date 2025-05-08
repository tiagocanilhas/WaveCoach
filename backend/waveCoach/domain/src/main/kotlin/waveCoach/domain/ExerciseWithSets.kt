package waveCoach.domain

data class ExerciseWithSets(
    val id: Int,
    val activity: Int,
    val exercise: Int,
    val exerciseOrder: Int,
    val sets: List<Sets>,
)
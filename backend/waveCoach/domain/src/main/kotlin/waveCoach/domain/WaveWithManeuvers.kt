package waveCoach.domain

data class WaveWithManeuvers(
    val id: Int,
    val points: Float?,
    val maneuvers: List<Maneuver>,
    val order: Int,
)
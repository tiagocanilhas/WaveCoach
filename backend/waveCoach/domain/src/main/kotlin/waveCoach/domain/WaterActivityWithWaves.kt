package waveCoach.domain

data class WaterActivityWithWaves(
    val id: Int,
    val athleteId: Int,
    val date: Long,
    val microcycleId: Int,
    val rpe: Int,
    val condition: String,
    val trimp: Int,
    val duration: Int,
    val waves: List<WaveWithManeuvers>,
)

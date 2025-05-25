package waveCoach.domain

data class WaterActivityWithWaves(
    val id: Int,
    val athleteId: Int,
    val date: Long,
    val microcycleId: Int,
    val pse: Int,
    val condition: String,
    val heartRate: Int,
    val duration: Int,
    val waves: List<WaveWithManeuvers>,
)

package waveCoach.domain

data class CompetitionWithHeats(
    val id: Int,
    val uid: Int,
    val date: Long,
    val location: String,
    val place: Int,
    val heats: List<HeatWithWaterActivity>
)
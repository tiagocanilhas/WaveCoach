package waveCoach.http.model.output

data class CompetitionOutputModel(
    val id: Int,
    val uid: Int,
    val date: Long,
    val location: String,
    val place: Int,
    val name: String,
    val heats: List<HeatOutputModel>,
)
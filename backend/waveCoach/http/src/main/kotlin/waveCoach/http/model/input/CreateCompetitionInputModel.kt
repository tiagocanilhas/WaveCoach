package waveCoach.http.model.input

data class CreateCompetitionInputModel(
    val date: String,
    val location: String,
    val place: Int,
    val heats: List<HeatInputModel>,
)
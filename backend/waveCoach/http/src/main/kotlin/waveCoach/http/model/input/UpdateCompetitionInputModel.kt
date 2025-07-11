package waveCoach.http.model.input

data class UpdateCompetitionInputModel(
    val date: String?,
    val location: String?,
    val place: Int?,
    val heats: List<UpdateHeatInputModel>?
)
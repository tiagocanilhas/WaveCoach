package waveCoach.http.model.input

data class UpdateHeatInputModel(
    val id: Int?,
    val score: Int?,
    val waterActivity: UpdateCompetitionWaterActivityInputModel?,
)

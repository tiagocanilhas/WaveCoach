package waveCoach.http.model.input

data class UpdateCompetitionWaterActivityInputModel(
    val id: Int?,
    val rpe: Int?,
    val condition: String?,
    val trimp: Int?,
    val duration: Int?,
    val waves: List<UpdateCompetitionWaveInputModel>?,
)
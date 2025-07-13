package waveCoach.http.model.input

data class UpdateWaterActivityInputModel(
    val date: String?,
    val rpe: Int?,
    val condition: String?,
    val trimp: Int?,
    val duration: Int?,
    val waves: List<UpdateWaveInputModel>?,
)

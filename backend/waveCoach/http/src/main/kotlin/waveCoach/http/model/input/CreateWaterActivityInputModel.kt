package waveCoach.http.model.input

data class CreateWaterActivityInputModel(
    val athleteId: Int,
    val date: String,
    val rpe: Int,
    val condition: String,
    val trimp: Int,
    val duration: Int,
    val waves: List<WaveInputModel>,
)

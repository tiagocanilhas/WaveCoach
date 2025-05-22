package waveCoach.http.model.input

data class CreateWaterActivityInputModel(
    val athleteId: Int,
    val date: String,
    val pse: Int,
    val condition: String,
    val heartRate: Int,
    val duration: Int,
    val waves: List<WaveInputModel>,
)
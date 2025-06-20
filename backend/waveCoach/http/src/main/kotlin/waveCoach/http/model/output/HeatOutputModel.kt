package waveCoach.http.model.output

data class HeatOutputModel(
    val id: Int,
    val score: Float,
    val waterActivity: WaterActivityOutputModel
)
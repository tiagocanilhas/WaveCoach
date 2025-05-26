package waveCoach.http.model.output

data class MesocycleWaterOutputModel (
    val id: Int,
    val startTime: Long,
    val endTime: Long,
    val microcycles: List<MicrocycleWaterOutputModel>
)
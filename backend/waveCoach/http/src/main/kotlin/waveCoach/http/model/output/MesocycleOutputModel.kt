package waveCoach.http.model.output

data class MesocycleOutputModel(
    val id: Int,
    val startTime: Long,
    val endTime: Long,
    val microcycles: List<MicrocycleOutputModel>,
)

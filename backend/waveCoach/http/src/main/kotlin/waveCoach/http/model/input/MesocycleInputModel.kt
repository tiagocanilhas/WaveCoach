package waveCoach.http.model.input

data class MesocycleInputModel (
    val id: Int?,
    val startTime: Long,
    val endTime: Long,
    val microcycles: List<MicrocycleInputModel>,
)
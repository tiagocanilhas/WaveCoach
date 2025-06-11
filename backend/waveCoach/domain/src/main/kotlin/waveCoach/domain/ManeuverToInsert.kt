package waveCoach.domain

data class ManeuverToInsert(
    val waveId: Int,
    val waterManeuverId: Int,
    val success: Boolean,
    val order: Int,
)
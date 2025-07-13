package waveCoach.domain

data class ManeuverToUpdate(
    val id: Int,
    val waterManeuverId: Int?,
    val success: Boolean?,
    val order: Int?,
)

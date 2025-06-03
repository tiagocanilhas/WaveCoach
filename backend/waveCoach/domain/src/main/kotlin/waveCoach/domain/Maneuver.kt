package waveCoach.domain

data class Maneuver(
    val id: Int,
    val waterManeuverId: Int,
    val waterManeuverName: String,
    val url: String?,
    val success: Boolean,
    val order: Int,
)

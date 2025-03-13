package waveCoach.domain

import kotlinx.datetime.Instant

data class Token(
    val tokenValidationInfo: TokenValidationInfo,
    val uid: Int,
    val createdTime: Instant,
    val usedTime: Instant,
)
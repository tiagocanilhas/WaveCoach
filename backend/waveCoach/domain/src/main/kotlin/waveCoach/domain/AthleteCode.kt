package waveCoach.domain

import kotlinx.datetime.Instant

class AthleteCode(
    val uid: Int,
    val codeValidationInfo: CodeValidationInfo,
    val createdTime: Instant,
)

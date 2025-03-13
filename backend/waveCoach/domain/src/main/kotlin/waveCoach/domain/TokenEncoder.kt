package waveCoach.domain

interface TokenEncoder {
    fun createValidationInformation(token: String): TokenValidationInfo
}
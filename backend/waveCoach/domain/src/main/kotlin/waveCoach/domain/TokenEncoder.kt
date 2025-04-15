package waveCoach.domain

interface TokenEncoder {
    fun createValidationInformation(token: String): TokenValidationInfo

    fun createCodeValidationInformation(code: String): CodeValidationInfo
}

package waveCoach.domain

import waveCoach.domain.TokenEncoder
import java.security.MessageDigest
import java.util.Base64

class Sha256TokenEncoder : TokenEncoder {
    override fun createValidationInformation(token: String): TokenValidationInfo = TokenValidationInfo(hash(token))

    private fun hash(input: String): String {
        val messageDigest = MessageDigest.getInstance("SHA256")
        val hash = messageDigest.digest(Charsets.UTF_8.encode(input).array())
        return Base64.getUrlEncoder().encodeToString(hash)
    }
}
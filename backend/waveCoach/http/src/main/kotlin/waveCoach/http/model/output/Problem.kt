package waveCoach.http.model.output

import org.springframework.http.ResponseEntity
import java.net.URI

class Problem(
    val title: String,
    typeUri: URI,
) {
    val type = typeUri.toASCIIString()

    companion object {
        const val MEDIA_TYPE = "application/problem+json"

        fun response(status: Int, problem: Problem) = ResponseEntity
            .status(status)
            .header("Content-Type", MEDIA_TYPE)
            .body<Any>(problem)

        private const val BASE_URI = ""

        val insecurePassword = Problem(
            "Insecure password",
            URI("$BASE_URI/insecure-password")
        )

        val invalidLogin = Problem(
            "Invalid login",
            URI("$BASE_URI/invalid-login")
        )

        val invalidToken = Problem(
            "Invalid token",
            URI("$BASE_URI/invalid-token")
        )

        val passwordIsBlank = Problem(
            "Password is blank",
            URI("$BASE_URI/password-is-blank")
        )

        val tokenNotFound = Problem(
            "Token not found",
            URI("$BASE_URI/token-not-found")
        )

        val usernameAlreadyExists = Problem(
            "Username already exists",
            URI("$BASE_URI/username-already-exists")
        )

        val usernameIsBlank = Problem(
            "Username is blank",
            URI("$BASE_URI/username-is-blank")
        )

        val invalidBirthDate = Problem(
            "Invalid birth date",
            URI.create("$BASE_URI/invalid-birth-date")
        )

        val invalidName = Problem(
            "Invalid name",
            URI.create("$BASE_URI/invalid-name")
        )
    }
}
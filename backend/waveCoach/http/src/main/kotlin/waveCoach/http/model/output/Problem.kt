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
            URI.create("$BASE_URI/insecure-password")
        )

        val usernameAlreadyExists = Problem(
            "Username already exists",
            URI.create("$BASE_URI/username-already-exists")
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
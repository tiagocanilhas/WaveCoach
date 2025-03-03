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

        val xpto = Problem(
            "xpto",
            URI("$BASE_URI/xpto"),
        )
    }
}
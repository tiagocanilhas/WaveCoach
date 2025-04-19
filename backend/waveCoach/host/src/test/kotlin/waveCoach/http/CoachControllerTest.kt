package waveCoach.http

import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import waveCoach.host.WaveCoachApplication
import waveCoach.http.model.output.Problem
import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.Test

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [WaveCoachApplication::class])
class CoachControllerTest {
    @LocalServerPort
    var port: Int = 0

    val BASE_URL: String
        get() = "http://localhost:$port/api"

    /**
     * Create Coach Tests
     */

    @Test
    fun `create a coach - success`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "username" to randomString(),
                "password" to randomString(),
            )

        client.post().uri("/coaches")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isCreated
            .expectHeader().exists("Location")
            .expectHeader().value("location") {
                assertTrue(it.startsWith("/api/coaches/"))
            }
    }

    @Test
    fun `create a coach - invalid username`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val invalidUsernames =
            listOf(
                "", // empty
                "aaa", // smaller than 4 characters
                "a".repeat(64), // bigger than 63 characters
            )

        invalidUsernames.forEach { username ->
            val body =
                mapOf(
                    "username" to username,
                    "password" to randomString(),
                )

            client.post().uri("/coaches")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isBadRequest
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .jsonPath("type").isEqualTo(Problem.invalidUsername.type.toString())
        }
    }

    @Test
    fun `create a coach - insecure password`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val insecurePasswords =
            listOf(
                "", // empty
                "Abc1234", // missing special character
                "abc123!", // missing uppercase letter
                "ABC123!", // missing lowercase letter
                "Abc!@#", // missing number
                "Abc12!", // smaller than 6 characters
            )

        insecurePasswords.forEach { password ->
            val body =
                mapOf(
                    "username" to randomString(),
                    "password" to password,
                )

            client.post().uri("/coaches")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isBadRequest
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .jsonPath("type").isEqualTo(Problem.insecurePassword.type.toString())
        }
    }

    @Test
    fun `create a coach - username already exists`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "username" to USERNAME_OF_ADMIN,
                "password" to randomString(),
            )

        client.post().uri("/coaches")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.usernameAlreadyExists.type.toString())
    }

    companion object {
        private val USERNAME_OF_ADMIN = "admin"

        private fun randomString() = "String_${abs(Random.nextLong())}"
    }
}

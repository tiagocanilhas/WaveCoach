package waveCoach.http

import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.MediaType
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.test.web.reactive.server.WebTestClient
import waveCoach.host.WaveCoachApplication
import waveCoach.http.model.output.Problem
import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.Test

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [WaveCoachApplication::class])
class WaterManeuverControllerTest {
    @LocalServerPort
    var port: Int = 0

    val BASE_URL: String
        get() = "http://localhost:$port/api"

    /**
     * Create Water Maneuver Test
     */

    @Test
    fun `create water maneuver - success`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val input =
            mapOf(
                "name" to randomString(),
            )

        val body =
            MultipartBodyBuilder().apply {
                part("input", input)
            }.build()

        client.post()
            .uri("/water/maneuver")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .bodyValue(body)
            .exchange()
            .expectStatus().isCreated
            .expectHeader().exists("Location")
            .expectHeader().value("location") {
                assertTrue(it.startsWith("/api/water/maneuver/"))
            }
    }

    @Test
    fun `create water maneuver - unauthorized`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val input =
            mapOf(
                "name" to randomString(),
            )

        val body =
            MultipartBodyBuilder().apply {
                part("input", input)
            }.build()

        client.post()
            .uri("/water/maneuver")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .bodyValue(body)
            .exchange()
            .expectStatus().isUnauthorized
    }

    @Test
    fun `create water maneuver - invalid name`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val input =
            mapOf(
                "name" to "",
            )

        val body =
            MultipartBodyBuilder().apply {
                part("input", input)
            }.build()

        client.post()
            .uri("/water/maneuver")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .bodyValue(body)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidName.type.toString())
    }

    @Test
    fun `create water maneuver - name already exists`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val input =
            mapOf(
                "name" to "Roll",
            )

        val body =
            MultipartBodyBuilder().apply {
                part("input", input)
            }.build()

        client.post()
            .uri("/water/maneuver")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .bodyValue(body)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.nameAlreadyExists.type.toString())
    }

    @Test
    fun `create water maneuver - user is not a coach`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val input =
            mapOf(
                "name" to randomString(),
            )

        val body =
            MultipartBodyBuilder().apply {
                part("input", input)
            }.build()

        client.post()
            .uri("/water/maneuver")
            .header("Authorization", "Bearer $FIRST_ATHLETE_TOKEN")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .bodyValue(body)
            .exchange()
            .expectStatus().isForbidden
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.userIsNotACoach.type.toString())
    }

    /**
     * Get Water Maneuvers Test
     */

    @Test
    fun `get all water maneuvers - success`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get()
            .uri("/water/maneuver")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("maneuvers[0].id").isEqualTo(1)
            .jsonPath("maneuvers[0].name").isEqualTo("Roll")
            .jsonPath("maneuvers[1].id").isEqualTo(2)
            .jsonPath("maneuvers[1].name").isEqualTo("360")
            .jsonPath("maneuvers[2].id").isEqualTo(3)
            .jsonPath("maneuvers[2].name").isEqualTo("360i")
    }

    @Test
    fun `get all water maneuvers - unauthorized`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get()
            .uri("/water/maneuver")
            .exchange()
            .expectStatus().isUnauthorized
    }

    /**
     * Get Water Maneuver Test
     */

    /**
     * Update Water Maneuver Test
     */

    /**
     * Delete Water Maneuver Test
     */

    companion object {
        private const val FIRST_COACH_TOKEN = "i_aY-4lpMqAIMuhkimTbKy4xYEuyvgFPaaTpVS0lctQ="

        private const val FIRST_ATHLETE_TOKEN = "0FaEBvcKLwE1YKrLYdhHd5p61EQtJThf3mEX6o28Lgo="

        private fun randomString() = "String_${abs(Random.nextLong())}"
    }
}

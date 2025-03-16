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
class AthleteControllerTest {
    @LocalServerPort
    var port: Int = 0

    val BASE_URL: String
        get() = "http://localhost:$port/api"

    @Test
    fun `create an athlete - success`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body = mapOf(
            "name" to randomString(),
            "birthDate" to VALID_BIRTHDATE,
        )

        client.post().uri("/athletes")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isCreated
            .expectHeader().exists("Location")
            .expectHeader().value("location") {
                assertTrue(it.startsWith("/api/athletes/"))
            }
    }

    @Test
    fun `create an athlete - invalid birth date`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body = mapOf(
            "name" to randomString(),
            "birthDate" to INVALID_BIRTHDATE,
        )

        client.post().uri("/athletes")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidBirthDate.type.toString())
    }

    @Test
    fun `create an athlete - invalid name`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body = mapOf(
            "name" to "",
            "birthDate" to VALID_BIRTHDATE,
        )

        client.post().uri("/athletes")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidName.type.toString())
    }

    @Test
    fun `remove an athlete - success`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.delete().uri("/athletes/$ATHLETE_ID")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .exchange()
            .expectStatus().isNoContent
    }

    @Test
    fun `remove an athlete - invalid athlete id`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.delete().uri("/athletes/invalid")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidAthleteId.type.toString())
    }

    @Test
    fun `remove an athlete - athlete not found`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.delete().uri("/athletes/0")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .exchange()
            .expectStatus().isNotFound
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.athleteNotFound.type.toString())
    }

    @Test
    fun `remove an athlete - not athlete's coach`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.delete().uri("/athletes/$ATHLETE_ID")
            .header("Authorization", "Bearer $SECOND_COACH_TOKEN")
            .exchange()
            .expectStatus().isForbidden
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.notAthletesCoach.type.toString())
    }

    companion object {
        private fun randomString() = "String_${abs(Random.nextLong())}"

        private const val VALID_BIRTHDATE = "2000-01-01"
        private const val INVALID_BIRTHDATE = "2000-01-32"
        private const val ATHLETE_ID = 3
        private const val FIRST_COACH_TOKEN = "i_aY-4lpMqAIMuhkimTbKy4xYEuyvgFPaaTpVS0lctQ="
        private const val SECOND_COACH_TOKEN = "fM5JjtPOUqtnZg1lB7jnJhXBP5gI2WbIIBoO3JhYM5M="
    }
}

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

    /**
     * Create Athlete Tests
     */

    @Test
    fun `create an athlete - success`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body = mapOf(
            "name" to randomString(),
            "birthDate" to VALID_DATE,
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
            "birthDate" to INVALID_DATE,
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

        val invalidNames = listOf(
            "",
            "a".repeat(65),
        )

        invalidNames.forEach { name ->
            val body = mapOf(
                "name" to name,
                "birthDate" to VALID_DATE,
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
    }



    /**
     * Remove Athlete Tests
     */

    @Test
    fun `remove an athlete - success`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.delete().uri("/athletes/$FIRST_ATHLETE_ID")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .exchange()
            .expectStatus().isNoContent
    }

    @Test
    fun `remove an athlete - invalid athlete id`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val id = "invalid"

        client.delete().uri("/athletes/$id")
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

        val id = 0

        client.delete().uri("/athletes/$id")
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

        client.delete().uri("/athletes/$FIRST_ATHLETE_ID")
            .header("Authorization", "Bearer $SECOND_COACH_TOKEN")
            .exchange()
            .expectStatus().isForbidden
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.notAthletesCoach.type.toString())
    }



    /**
     * Create Characteristics Tests
     */

    @Test
    fun `create characteristics - success`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body = mapOf(
            "date" to VALID_DATE,
            "height" to 1,
            "weight" to 1.0,
            "calories" to 1,
            "waist" to 1,
            "arm" to 1,
            "thigh" to 1,
            "tricep" to 1.0,
            "abdominal" to 1.0,
        )

        client.post().uri("/athletes/$SECOND_ATHLETE_ID/characteristics")
            .header("Authorization", "Bearer $SECOND_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isCreated
            .expectBody()
            .jsonPath("uid").isEqualTo(SECOND_ATHLETE_ID)
    }

    @Test
    fun `create characteristics - invalid date`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body = mapOf(
            "date" to INVALID_DATE,
            "height" to 1,
            "weight" to 1.0,
            "calories" to 1,
            "waist" to 1,
            "arm" to 1,
            "thigh" to 1,
            "tricep" to 1.0,
            "abdominal" to 1.0,
        )

        client.post().uri("/athletes/$SECOND_ATHLETE_ID/characteristics")
            .header("Authorization", "Bearer $SECOND_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidDate.type.toString())
    }

    @Test
    fun `create characteristics - invalid characteristics`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body = mapOf(
            "date" to VALID_DATE,
            "height" to 1,
            "weight" to 1.0,
            "calories" to 1,
            "waist" to 1,
            "arm" to 1,
            "thigh" to 1,
            "tricep" to 1.0,
            "abdominal" to 1.0,
        )

        val invalidCharacteristics = listOf(
            body + ("height" to -1),
            body + ("weight" to -1.0),
            body + ("calories" to -1),
            body + ("waist" to -1),
            body + ("arm" to -1),
            body + ("thigh" to -1),
            body + ("tricep" to -1.0),
            body + ("abdominal" to -1.0),
        )

        invalidCharacteristics.forEach { characteristics ->
            client.post().uri("/athletes/$SECOND_ATHLETE_ID/characteristics")
                .header("Authorization", "Bearer $SECOND_COACH_TOKEN")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(characteristics)
                .exchange()
                .expectStatus().isBadRequest
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .jsonPath("type").isEqualTo(Problem.invalidCharacteristics.type.toString())
        }
    }

    @Test
    fun `create characteristics - invalid athlete id`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val id = "invalid"

        val body = mapOf(
            "date" to VALID_DATE,
            "height" to 1,
            "weight" to 1.0,
            "calories" to 1,
            "waist" to 1,
            "arm" to 1,
            "thigh" to 1,
            "tricep" to 1.0,
            "abdominal" to 1.0,
        )

        client.post().uri("/athletes/$id/characteristics")
            .header("Authorization", "Bearer $SECOND_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidAthleteId.type.toString())
    }

    @Test
    fun `create characteristics - athlete not found`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val id = 0

        val body = mapOf(
            "date" to VALID_DATE,
            "height" to 1,
            "weight" to 1.0,
            "calories" to 1,
            "waist" to 1,
            "arm" to 1,
            "thigh" to 1,
            "tricep" to 1.0,
            "abdominal" to 1.0,
        )

        client.post().uri("/athletes/$id/characteristics")
            .header("Authorization", "Bearer $SECOND_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isNotFound
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.athleteNotFound.type.toString())
    }

    @Test
    fun `create characteristics - not athlete's coach`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body = mapOf(
            "date" to VALID_DATE,
            "height" to 1,
            "weight" to 1.0,
            "calories" to 1,
            "waist" to 1,
            "arm" to 1,
            "thigh" to 1,
            "tricep" to 1.0,
            "abdominal" to 1.0,
        )

        client.post().uri("/athletes/$SECOND_ATHLETE_ID/characteristics")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isForbidden
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.notAthletesCoach.type.toString())
    }

    @Test
    fun `update characteristics - success`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body = mapOf(
            "date" to VALID_DATE,
            "height" to 1,
            "weight" to 1.0,
            "calories" to 1,
            "waist" to 1,
            "arm" to 1,
            "thigh" to 1,
            "tricep" to 1.0,
            "abdominal" to 1.0,
        )

        client.put().uri("/athletes/$SECOND_ATHLETE_ID/characteristics")
            .header("Authorization", "Bearer $SECOND_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isNoContent
    }

    @Test
    fun `update characteristics - invalid date`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body = mapOf(
            "date" to INVALID_DATE,
            "height" to 1,
            "weight" to 1.0,
            "calories" to 1,
            "waist" to 1,
            "arm" to 1,
            "thigh" to 1,
            "tricep" to 1.0,
            "abdominal" to 1.0,
        )

        client.put().uri("/athletes/$SECOND_ATHLETE_ID/characteristics")
            .header("Authorization", "Bearer $SECOND_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidDate.type.toString())
    }

    @Test
    fun `update characteristics - invalid characteristics`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body = mapOf(
            "date" to VALID_DATE,
            "height" to 1,
            "weight" to 1.0,
            "calories" to 1,
            "waist" to 1,
            "arm" to 1,
            "thigh" to 1,
            "tricep" to 1.0,
            "abdominal" to 1.0,
        )

        val invalidCharacteristics = listOf(
            body + ("height" to -1),
            body + ("weight" to -1.0),
            body + ("calories" to -1),
            body + ("waist" to -1),
            body + ("arm" to -1),
            body + ("thigh" to -1),
            body + ("tricep" to -1.0),
            body + ("abdominal" to -1.0),
        )

        invalidCharacteristics.forEach { characteristics ->
            client.put().uri("/athletes/$SECOND_ATHLETE_ID/characteristics")
                .header("Authorization", "Bearer $SECOND_COACH_TOKEN")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(characteristics)
                .exchange()
                .expectStatus().isBadRequest
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .jsonPath("type").isEqualTo(Problem.invalidCharacteristics.type.toString())
        }
    }

    @Test
    fun `update characteristics - invalid athlete id`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val id = "invalid"

        val body = mapOf(
            "date" to VALID_DATE,
            "height" to 1,
            "weight" to 1.0,
            "calories" to 1,
            "waist" to 1,
            "arm" to 1,
            "thigh" to 1,
            "tricep" to 1.0,
            "abdominal" to 1.0,
        )

        client.put().uri("/athletes/$id/characteristics")
            .header("Authorization", "Bearer $SECOND_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidAthleteId.type.toString())
    }

    @Test
    fun `update characteristics - athlete not found`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val id = 0

        val body = mapOf(
            "date" to VALID_DATE,
            "height" to 1,
            "weight" to 1.0,
            "calories" to 1,
            "waist" to 1,
            "arm" to 1,
            "thigh" to 1,
            "tricep" to 1.0,
            "abdominal" to 1.0,
        )

        client.put().uri("/athletes/$id/characteristics")
            .header("Authorization", "Bearer $SECOND_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isNotFound
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.athleteNotFound.type.toString())
    }

    @Test
    fun `update characteristics - not athlete's coach`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body = mapOf(
            "date" to VALID_DATE,
            "height" to 1,
            "weight" to 1.0,
            "calories" to 1,
            "waist" to 1,
            "arm" to 1,
            "thigh" to 1,
            "tricep" to 1.0,
            "abdominal" to 1.0,
        )

        client.put().uri("/athletes/$SECOND_ATHLETE_ID/characteristics")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isForbidden
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.notAthletesCoach.type.toString())
    }

    companion object {
        private fun randomString() = "String_${abs(Random.nextLong())}"

        private const val VALID_DATE = "01-01-2000"
        private const val INVALID_DATE = "32-01-2000"
        private const val FIRST_ATHLETE_ID = 3
        private const val SECOND_ATHLETE_ID = 4
        private const val FIRST_COACH_TOKEN = "i_aY-4lpMqAIMuhkimTbKy4xYEuyvgFPaaTpVS0lctQ="
        private const val SECOND_COACH_TOKEN = "fM5JjtPOUqtnZg1lB7jnJhXBP5gI2WbIIBoO3JhYM5M="
    }
}

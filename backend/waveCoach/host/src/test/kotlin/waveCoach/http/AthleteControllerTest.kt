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
     * Get Athlete Tests
     */

    @Test
    fun `get an athlete - success`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get().uri("/athletes/$FIRST_ATHLETE_ID")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("uid").isEqualTo(FIRST_ATHLETE_ID)
            .jsonPath("coach").isEqualTo(FIRST_COACH_ID)
            .jsonPath("name").isEqualTo(FIRST_ATHLETE_NAME)
            .jsonPath("birthDate").isEqualTo(FIRST_ATHLETE_BIRTH_DATE)
    }

    @Test
    fun `get an athlete - invalid athlete id`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val id = "invalid"

        client.get().uri("/athletes/$id")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidAthleteId.type.toString())
    }

    @Test
    fun `get an athlete - athlete not found`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val id = 0

        client.get().uri("/athletes/$id")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .exchange()
            .expectStatus().isNotFound
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.athleteNotFound.type.toString())
    }

    @Test
    fun `get an athlete - not athlete's coach`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get().uri("/athletes/$FIRST_ATHLETE_ID")
            .header("Authorization", "Bearer $SECOND_COACH_TOKEN")
            .exchange()
            .expectStatus().isForbidden
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.notAthletesCoach.type.toString())
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
            .expectHeader().exists("Location")
            .expectHeader().value("location") {
                assertTrue(it.startsWith("/api/athletes/$SECOND_ATHLETE_ID/characteristics/${946684800000}"))
            }
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
    fun `create characteristics - characteristics already exists`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body = mapOf(
            "date" to ATHLETE_CHARACTERISTICS_FIRST_DATE,
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
            .expectStatus().isEqualTo(409)
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.characteristicsAlreadyExists.type.toString())
    }

    /**
     * Get Characteristics Tests
     */

    @Test
    fun `get characteristics - success`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get().uri("/athletes/$SECOND_ATHLETE_ID/characteristics/$ATHLETE_CHARACTERISTICS_FIRST_DATE")
            .header("Authorization", "Bearer $SECOND_COACH_TOKEN")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("date").isEqualTo(ATHLETE_CHARACTERISTICS_FIRST_DATE_LONG)
            .jsonPath("uid").isEqualTo(SECOND_ATHLETE_ID)
            .jsonPath("height").isEqualTo(ATHLETE_HEIGHT)
            .jsonPath("weight").isEqualTo(ATHLETE_WEIGHT)
            .jsonPath("calories").isEqualTo(ATHLETE_CALORIES)
            .jsonPath("waist").isEqualTo(ATHLETE_WAIST)
            .jsonPath("arm").isEqualTo(ATHLETE_ARM)
            .jsonPath("thigh").isEqualTo(ATHLETE_THIGH)
            .jsonPath("tricep").isEqualTo(ATHLETE_TRICEP)
            .jsonPath("abdominal").isEqualTo(ATHLETE_ABDOMINAL)
    }

    @Test
    fun `get characteristics - invalid date`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get().uri("/athletes/$SECOND_ATHLETE_ID/characteristics/$INVALID_DATE")
            .header("Authorization", "Bearer $SECOND_COACH_TOKEN")
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidDate.type.toString())
    }

    @Test
    fun `get characteristics - invalid athlete id`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val id = "invalid"

        client.get().uri("/athletes/$id/characteristics/$ATHLETE_CHARACTERISTICS_FIRST_DATE")
            .header("Authorization", "Bearer $SECOND_COACH_TOKEN")
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidAthleteId.type.toString())
    }

    @Test
    fun `get characteristics - athlete not found`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val id = 0

        client.get().uri("/athletes/$id/characteristics/$ATHLETE_CHARACTERISTICS_FIRST_DATE")
            .header("Authorization", "Bearer $SECOND_COACH_TOKEN")
            .exchange()
            .expectStatus().isNotFound
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.athleteNotFound.type.toString())
    }

    @Test
    fun `get characteristics - not athlete's coach`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get().uri("/athletes/$SECOND_ATHLETE_ID/characteristics/$ATHLETE_CHARACTERISTICS_FIRST_DATE")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .exchange()
            .expectStatus().isForbidden
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.notAthletesCoach.type.toString())
    }

    @Test
    fun `get characteristics - characteristics not found`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get().uri("/athletes/$SECOND_ATHLETE_ID/characteristics/$ANOTHER_VALID_DATE")
            .header("Authorization", "Bearer $SECOND_COACH_TOKEN")
            .exchange()
            .expectStatus().isNotFound
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.characteristicsNotFound.type.toString())
    }

    /**
     * Get Characteristics List Tests
     */

    @Test
    fun `get characteristics list - success`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get().uri("/athletes/$SECOND_ATHLETE_ID/characteristics")
            .header("Authorization", "Bearer $SECOND_COACH_TOKEN")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("characteristics").exists()
            .jsonPath("characteristics.length()").value<Int> { assertTrue(it > 0) }
    }

    @Test
    fun `get characteristics list - invalid athlete id`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val id = "invalid"

        client.get().uri("/athletes/$id/characteristics")
            .header("Authorization", "Bearer $SECOND_COACH_TOKEN")
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidAthleteId.type.toString())
    }

    @Test
    fun `get characteristics list - athlete not found`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val id = 0

        client.get().uri("/athletes/$id/characteristics")
            .header("Authorization", "Bearer $SECOND_COACH_TOKEN")
            .exchange()
            .expectStatus().isNotFound
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.athleteNotFound.type.toString())
    }

    @Test
    fun `get characteristics list - not athlete's coach`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get().uri("/athletes/$SECOND_ATHLETE_ID/characteristics")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .exchange()
            .expectStatus().isForbidden
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.notAthletesCoach.type.toString())
    }

    /**
     * Update Characteristics Tests
     */

    @Test
    fun `update characteristics - success`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body = mapOf(
            "height" to 1,
            "weight" to 1.0,
            "calories" to 1,
            "waist" to 1,
            "arm" to 1,
            "thigh" to 1,
            "tricep" to 1.0,
            "abdominal" to 1.0,
        )

        client.put().uri("/athletes/$SECOND_ATHLETE_ID/characteristics/$VALID_DATE")
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
            "height" to 1,
            "weight" to 1.0,
            "calories" to 1,
            "waist" to 1,
            "arm" to 1,
            "thigh" to 1,
            "tricep" to 1.0,
            "abdominal" to 1.0,
        )

        client.put().uri("/athletes/$SECOND_ATHLETE_ID/characteristics/$INVALID_DATE")
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
            client.put().uri("/athletes/$SECOND_ATHLETE_ID/characteristics/$VALID_DATE")
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
            "height" to 1,
            "weight" to 1.0,
            "calories" to 1,
            "waist" to 1,
            "arm" to 1,
            "thigh" to 1,
            "tricep" to 1.0,
            "abdominal" to 1.0,
        )

        client.put().uri("/athletes/$id/characteristics/$VALID_DATE")
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
            "height" to 1,
            "weight" to 1.0,
            "calories" to 1,
            "waist" to 1,
            "arm" to 1,
            "thigh" to 1,
            "tricep" to 1.0,
            "abdominal" to 1.0,
        )

        client.put().uri("/athletes/$id/characteristics/$VALID_DATE")
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
            "height" to 1,
            "weight" to 1.0,
            "calories" to 1,
            "waist" to 1,
            "arm" to 1,
            "thigh" to 1,
            "tricep" to 1.0,
            "abdominal" to 1.0,
        )

        client.put().uri("/athletes/$SECOND_ATHLETE_ID/characteristics/$VALID_DATE")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isForbidden
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.notAthletesCoach.type.toString())
    }

    /**
     * Remove Characteristics Tests
     */

    @Test
    fun `remove characteristics - success`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.delete().uri("/athletes/$SECOND_ATHLETE_ID/characteristics/$ATHLETE_CHARACTERISTICS_SECOND_DATE")
            .header("Authorization", "Bearer $SECOND_COACH_TOKEN")
            .exchange()
            .expectStatus().isNoContent
    }

    @Test
    fun `remove characteristics - invalid date`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.delete().uri("/athletes/$SECOND_ATHLETE_ID/characteristics/$INVALID_DATE")
            .header("Authorization", "Bearer $SECOND_COACH_TOKEN")
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidDate.type.toString())
    }

    @Test
    fun `remove characteristics - invalid athlete id`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val id = "invalid"

        client.delete().uri("/athletes/$id/characteristics/$ATHLETE_CHARACTERISTICS_FIRST_DATE")
            .header("Authorization", "Bearer $SECOND_COACH_TOKEN")
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidAthleteId.type.toString())
    }

    @Test
    fun `remove characteristics - athlete not found`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val id = 0

        client.delete().uri("/athletes/$id/characteristics/$ATHLETE_CHARACTERISTICS_FIRST_DATE")
            .header("Authorization", "Bearer $SECOND_COACH_TOKEN")
            .exchange()
            .expectStatus().isNotFound
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.athleteNotFound.type.toString())
    }

    @Test
    fun `remove characteristics - not athlete's coach`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.delete().uri("/athletes/$SECOND_ATHLETE_ID/characteristics/$ATHLETE_CHARACTERISTICS_FIRST_DATE")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .exchange()
            .expectStatus().isForbidden
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.notAthletesCoach.type.toString())
    }

    @Test
    fun `remove characteristics - characteristics not found`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.delete().uri("/athletes/$SECOND_ATHLETE_ID/characteristics/$ANOTHER_VALID_DATE")
            .header("Authorization", "Bearer $SECOND_COACH_TOKEN")
            .exchange()
            .expectStatus().isNotFound
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.characteristicsNotFound.type.toString())
    }

    companion object {
        private fun randomString() = "String_${abs(Random.nextLong())}"

        private const val VALID_DATE = "01-01-2000"
        private const val ANOTHER_VALID_DATE = "10-01-2000"
        private const val INVALID_DATE = "32-01-2000"
        private const val FIRST_ATHLETE_ID = 3
        private const val FIRST_ATHLETE_NAME = "John Doe"
        private const val FIRST_ATHLETE_BIRTH_DATE = 631152000
        private const val SECOND_ATHLETE_ID = 4
        private const val ATHLETE_CHARACTERISTICS_FIRST_DATE = "25-01-2000" // date long = 948758400000
        private const val ATHLETE_CHARACTERISTICS_FIRST_DATE_LONG = 948758400000
        private const val ATHLETE_CHARACTERISTICS_SECOND_DATE = "10-01-2000" // date long = 947462400000
        private const val FIRST_COACH_TOKEN = "i_aY-4lpMqAIMuhkimTbKy4xYEuyvgFPaaTpVS0lctQ="
        private const val FIRST_COACH_ID = 1
        private const val SECOND_COACH_TOKEN = "fM5JjtPOUqtnZg1lB7jnJhXBP5gI2WbIIBoO3JhYM5M="

        private const val ATHLETE_HEIGHT = 1
        private const val ATHLETE_WEIGHT = 1.0f
        private const val ATHLETE_CALORIES = 1
        private const val ATHLETE_WAIST = 1
        private const val ATHLETE_ARM = 1
        private const val ATHLETE_THIGH = 1
        private const val ATHLETE_TRICEP = 1.0f
        private const val ATHLETE_ABDOMINAL = 1.0f
    }
}

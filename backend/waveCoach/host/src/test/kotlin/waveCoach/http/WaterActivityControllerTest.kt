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
class WaterActivityControllerTest {
    @LocalServerPort
    var port: Int = 0

    val BASE_URL: String
        get() = "http://localhost:$port/api"

    /**
     * Create Water Activity Test
     */

    @Test
    fun `create water activity - success`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body = mapOf(
            "athleteId" to FIRST_ATHLETE_ID,
            "date" to DATE,
            "pse" to 5,
            "condition" to "good",
            "heartRate" to 120,
            "duration" to 60,
            "waves" to listOf(
                mapOf(
                    "points" to 10.0,
                    "maneuvers" to listOf(
                        mapOf(
                            "waterManeuverId" to 1,
                            "rightSide" to true,
                            "success" to true
                        ),
                        mapOf(
                            "waterManeuverId" to 2,
                            "rightSide" to false,
                            "success" to false
                        )
                    )
                )
            )
        )

        client.post()
            .uri("/water")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isCreated
            .expectHeader().exists("Location")
            .expectHeader().value("location") {
                assertTrue(it.startsWith("/api/water/"))
            }
    }

    @Test
    fun `create water activity - invalid date`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body = mapOf(
            "athleteId" to FIRST_ATHLETE_ID,
            "date" to "invalid-date",
            "pse" to 5,
            "condition" to "good",
            "heartRate" to 120,
            "duration" to 60,
            "waves" to listOf(
                mapOf(
                    "points" to 10.0,
                    "maneuvers" to listOf(
                        mapOf(
                            "waterManeuverId" to 1,
                            "rightSide" to true,
                            "success" to true
                        )
                    )
                )
            )
        )

        client.post()
            .uri("/water")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidDate.type.toString())
    }

    @Test
    fun `create water activity - athlete not found`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body = mapOf(
            "athleteId" to 0,
            "date" to DATE,
            "pse" to 5,
            "condition" to "good",
            "heartRate" to 120,
            "duration" to 60,
            "waves" to listOf(
                mapOf(
                    "points" to 10.0,
                    "maneuvers" to listOf(
                        mapOf(
                            "waterManeuverId" to 1,
                            "rightSide" to true,
                            "success" to true
                        )
                    )
                )
            )
        )

        client.post()
            .uri("/water")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isNotFound
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.athleteNotFound.type.toString())
    }

    @Test
    fun `create water activity - not athletes coach`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body = mapOf(
            "athleteId" to FIRST_ATHLETE_ID,
            "date" to DATE,
            "pse" to 5,
            "condition" to "good",
            "heartRate" to 120,
            "duration" to 60,
            "waves" to listOf(
                mapOf(
                    "points" to 10.0,
                    "maneuvers" to listOf(
                        mapOf(
                            "waterManeuverId" to 1,
                            "rightSide" to true,
                            "success" to true
                        )
                    )
                )
            )
        )

        client.post()
            .uri("/water")
            .header("Authorization", "Bearer $SECOND_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isForbidden
    }

    @Test
    fun `create water activity - activity without microcycle`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body = mapOf(
            "athleteId" to FIRST_ATHLETE_ID,
            "date" to DATE_WITHOUT_MICROCYCLE,
            "pse" to 5,
            "condition" to "good",
            "heartRate" to 120,
            "duration" to 60,
            "waves" to listOf(
                mapOf(
                    "points" to 10.0,
                    "maneuvers" to listOf(
                        mapOf(
                            "waterManeuverId" to 1,
                            "rightSide" to true,
                            "success" to true
                        )
                    )
                )
            )
        )

        client.post()
            .uri("/water")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.activityWithoutMicrocycle.type.toString())
    }

    @Test
    fun `create water activity - invalid pse`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body = mapOf(
            "athleteId" to FIRST_ATHLETE_ID,
            "date" to DATE,
            "pse" to -1,
            "condition" to "good",
            "heartRate" to 120,
            "duration" to 60,
            "waves" to listOf(
                mapOf(
                    "points" to 10.0,
                    "maneuvers" to listOf(
                        mapOf(
                            "waterManeuverId" to 1,
                            "rightSide" to true,
                            "success" to true
                        )
                    )
                )
            )
        )

        client.post()
            .uri("/water")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidPse.type.toString())
    }

    @Test
    fun `create water activity - invalid heart rate`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body = mapOf(
            "athleteId" to FIRST_ATHLETE_ID,
            "date" to DATE,
            "pse" to 5,
            "condition" to "good",
            "heartRate" to -1,
            "duration" to 60,
            "waves" to listOf(
                mapOf(
                    "points" to 10.0,
                    "maneuvers" to listOf(
                        mapOf(
                            "waterManeuverId" to 1,
                            "rightSide" to true,
                            "success" to true
                        )
                    )
                )
            )
        )

        client.post()
            .uri("/water")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidHeartRate.type.toString())
    }

    @Test
    fun `create water activity - invalid duration`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body = mapOf(
            "athleteId" to FIRST_ATHLETE_ID,
            "date" to DATE,
            "pse" to 5,
            "condition" to "good",
            "heartRate" to 120,
            "duration" to -1,
            "waves" to listOf(
                mapOf(
                    "points" to 10.0,
                    "maneuvers" to listOf(
                        mapOf(
                            "waterManeuverId" to 1,
                            "rightSide" to true,
                            "success" to true
                        )
                    )
                )
            )
        )

        client.post()
            .uri("/water")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidDuration.type.toString())
    }

    @Test
    fun `create water activity - invalid water maneuver`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body = mapOf(
            "athleteId" to FIRST_ATHLETE_ID,
            "date" to DATE,
            "pse" to 5,
            "condition" to "good",
            "heartRate" to 120,
            "duration" to 60,
            "waves" to listOf(
                mapOf(
                    "points" to 10.0,
                    "maneuvers" to listOf(
                        mapOf(
                            "waterManeuverId" to 0,
                            "rightSide" to true,
                            "success" to true
                        )
                    )
                )
            )
        )

        client.post()
            .uri("/water")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidWaterManeuver.type.toString())
    }

    /**
     * Get Water Activity Test
     */

    @Test
    fun `get water activity - success(coach)`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get()
            .uri("/water/2")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("id").isEqualTo(2)
            .jsonPath("athleteId").isEqualTo(FIRST_ATHLETE_ID)
            .jsonPath("microcycleId").isEqualTo(1)
            .jsonPath("date").isEqualTo(ACTIVITY_DATE)
            .jsonPath("pse").isEqualTo(5)
            .jsonPath("condition").isEqualTo("Good")
            .jsonPath("heartRate").isEqualTo(120)
            .jsonPath("duration").isEqualTo(60)
            .jsonPath("waves").exists()
    }

    @Test
    fun `get water activity - success(athlete)`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get()
            .uri("/water/2")
            .header("Authorization", "Bearer $FIRST_ATHLETE_TOKEN")
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("id").isEqualTo(2)
            .jsonPath("athleteId").isEqualTo(FIRST_ATHLETE_ID)
            .jsonPath("microcycleId").isEqualTo(1)
            .jsonPath("date").isEqualTo(ACTIVITY_DATE)
            .jsonPath("pse").isEqualTo(5)
            .jsonPath("condition").isEqualTo("Good")
            .jsonPath("heartRate").isEqualTo(120)
            .jsonPath("duration").isEqualTo(60)
            .jsonPath("waves").exists()
    }

    @Test
    fun `get water activity - not found`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get()
            .uri("/water/0")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .exchange()
            .expectStatus().isNotFound
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.waterActivityNotFound.type.toString())
    }

    @Test
    fun `get water activity - not athletes coach`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get()
            .uri("/water/2")
            .header("Authorization", "Bearer $SECOND_COACH_TOKEN")
            .exchange()
            .expectStatus().isForbidden
    }

    @Test
    fun `get water activity - invalid id`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get()
            .uri("/water/invalid-id")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidWaterActivityId.type.toString())
    }

    @Test
    fun `get water activity - unauthorized`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get()
            .uri("/water/2")
            .exchange()
            .expectStatus().isUnauthorized
    }

    @Test
    fun `get water activity - not water activity`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get()
            .uri("/water/1")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.notWaterActivity.type.toString())
    }



    companion object {
        private const val FIRST_COACH_TOKEN = "i_aY-4lpMqAIMuhkimTbKy4xYEuyvgFPaaTpVS0lctQ="
        private const val FIRST_COACH_ID = 1
        private const val SECOND_COACH_TOKEN = "fM5JjtPOUqtnZg1lB7jnJhXBP5gI2WbIIBoO3JhYM5M="
        private const val FIRST_ATHLETE_ID = 3
        private const val FIRST_ATHLETE_TOKEN = "0FaEBvcKLwE1YKrLYdhHd5p61EQtJThf3mEX6o28Lgo="

        private const val DATE = "03-05-2025" // date long = 1736006400000
        private const val ACTIVITY_DATE = 1746144000000
        private const val DATE_WITHOUT_MICROCYCLE = "01-01-2000" // date long = 1736006400000

        private fun randomString() = "String_${abs(Random.nextLong())}"
    }
}
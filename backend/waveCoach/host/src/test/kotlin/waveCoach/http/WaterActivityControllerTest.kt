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

        val body =
            mapOf(
                "athleteId" to FIRST_ATHLETE_ID,
                "date" to DATE,
                "rpe" to 5,
                "condition" to "good",
                "trimp" to 120,
                "duration" to 60,
                "waves" to
                    listOf(
                        mapOf(
                            "points" to null,
                            "maneuvers" to
                                listOf(
                                    mapOf(
                                        "waterManeuverId" to 1,
                                        "rightSide" to true,
                                        "success" to true,
                                    ),
                                    mapOf(
                                        "waterManeuverId" to 2,
                                        "rightSide" to false,
                                        "success" to false,
                                    ),
                                ),
                        ),
                    ),
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

        val body =
            mapOf(
                "athleteId" to FIRST_ATHLETE_ID,
                "date" to "invalid-date",
                "rpe" to 5,
                "condition" to "good",
                "trimp" to 120,
                "duration" to 60,
                "waves" to
                    listOf(
                        mapOf(
                            "points" to 10.0,
                            "maneuvers" to
                                listOf(
                                    mapOf(
                                        "waterManeuverId" to 1,
                                        "rightSide" to true,
                                        "success" to true,
                                    ),
                                ),
                        ),
                    ),
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

        val body =
            mapOf(
                "athleteId" to 0,
                "date" to DATE,
                "rpe" to 5,
                "condition" to "good",
                "trimp" to 120,
                "duration" to 60,
                "waves" to
                    listOf(
                        mapOf(
                            "points" to 10.0,
                            "maneuvers" to
                                listOf(
                                    mapOf(
                                        "waterManeuverId" to 1,
                                        "rightSide" to true,
                                        "success" to true,
                                    ),
                                ),
                        ),
                    ),
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

        val body =
            mapOf(
                "athleteId" to FIRST_ATHLETE_ID,
                "date" to DATE,
                "rpe" to 5,
                "condition" to "good",
                "trimp" to 120,
                "duration" to 60,
                "waves" to
                    listOf(
                        mapOf(
                            "points" to 10.0,
                            "maneuvers" to
                                listOf(
                                    mapOf(
                                        "waterManeuverId" to 1,
                                        "rightSide" to true,
                                        "success" to true,
                                    ),
                                ),
                        ),
                    ),
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

        val body =
            mapOf(
                "athleteId" to FIRST_ATHLETE_ID,
                "date" to DATE_WITHOUT_MICROCYCLE,
                "rpe" to 5,
                "condition" to "good",
                "trimp" to 120,
                "duration" to 60,
                "waves" to
                    listOf(
                        mapOf(
                            "points" to 10.0,
                            "maneuvers" to
                                listOf(
                                    mapOf(
                                        "waterManeuverId" to 1,
                                        "rightSide" to true,
                                        "success" to true,
                                    ),
                                ),
                        ),
                    ),
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
    fun `create water activity - invalid rpe`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "athleteId" to FIRST_ATHLETE_ID,
                "date" to DATE,
                "rpe" to -1,
                "condition" to "good",
                "trimp" to 120,
                "duration" to 60,
                "waves" to
                    listOf(
                        mapOf(
                            "points" to 10.0,
                            "maneuvers" to
                                listOf(
                                    mapOf(
                                        "waterManeuverId" to 1,
                                        "rightSide" to true,
                                        "success" to true,
                                    ),
                                ),
                        ),
                    ),
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
            .jsonPath("type").isEqualTo(Problem.invalidRpe.type.toString())
    }

    @Test
    fun `create water activity - invalid trimp`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "athleteId" to FIRST_ATHLETE_ID,
                "date" to DATE,
                "rpe" to 5,
                "condition" to "good",
                "trimp" to -1,
                "duration" to 60,
                "waves" to
                    listOf(
                        mapOf(
                            "points" to 10.0,
                            "maneuvers" to
                                listOf(
                                    mapOf(
                                        "waterManeuverId" to 1,
                                        "rightSide" to true,
                                        "success" to true,
                                    ),
                                ),
                        ),
                    ),
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
            .jsonPath("type").isEqualTo(Problem.invalidTrimp.type.toString())
    }

    @Test
    fun `create water activity - invalid duration`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "athleteId" to FIRST_ATHLETE_ID,
                "date" to DATE,
                "rpe" to 5,
                "condition" to "good",
                "trimp" to 120,
                "duration" to -1,
                "waves" to
                    listOf(
                        mapOf(
                            "points" to 10.0,
                            "maneuvers" to
                                listOf(
                                    mapOf(
                                        "waterManeuverId" to 1,
                                        "rightSide" to true,
                                        "success" to true,
                                    ),
                                ),
                        ),
                    ),
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

        val body =
            mapOf(
                "athleteId" to FIRST_ATHLETE_ID,
                "date" to DATE,
                "rpe" to 5,
                "condition" to "good",
                "trimp" to 120,
                "duration" to 60,
                "waves" to
                    listOf(
                        mapOf(
                            "points" to 10.0,
                            "maneuvers" to
                                listOf(
                                    mapOf(
                                        "waterManeuverId" to 0,
                                        "rightSide" to true,
                                        "success" to true,
                                    ),
                                ),
                        ),
                    ),
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
            .jsonPath("rpe").isEqualTo(5)
            .jsonPath("condition").isEqualTo("Good")
            .jsonPath("trimp").isEqualTo(120)
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
            .jsonPath("rpe").isEqualTo(5)
            .jsonPath("condition").isEqualTo("Good")
            .jsonPath("trimp").isEqualTo(120)
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

    /**
     * Update Water Activity Test
     */

    @Test
    fun `update water activity - success`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "date" to DATE,
                "rpe" to 6,
                "condition" to "excellent",
                "trimp" to 130,
                "duration" to 60,
                "waves" to
                    listOf(
                        mapOf(
                            "points" to 10.0,
                            "rightSide" to true,
                            "order" to 2,
                            "maneuvers" to
                                listOf(
                                    mapOf(
                                        "waterManeuverId" to 1,
                                        "success" to true,
                                    ),
                                    mapOf(
                                        "waterManeuverId" to 2,
                                        "success" to false,
                                    ),
                                ),
                        ),
                        mapOf(
                            "id" to 2,
                            "points" to 12.0,
                            "rightSide" to false,
                            "order" to 3,
                            "maneuvers" to
                                listOf(
                                    mapOf(
                                        "id" to 3,
                                        "waterManeuverId" to 2,
                                        "success" to false,
                                        "order" to 2,
                                    ),
                                ),
                        ),
                    ),
            )

        client.patch().uri("/water/$FIRST_ACTIVITY_ID")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isNoContent
    }

    @Test
    fun `update water activity - unauthorized`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "date" to DATE,
                "rpe" to 6,
                "condition" to "excellent",
                "trimp" to 130,
                "duration" to 60,
            )

        client.patch().uri("/water/$FIRST_ACTIVITY_ID")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isUnauthorized
    }

    @Test
    fun `update water activity - user is not a coach`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "date" to DATE,
                "rpe" to 6,
                "condition" to "excellent",
                "trimp" to 130,
                "duration" to 60,
            )

        client.patch().uri("/water/$FIRST_ACTIVITY_ID")
            .header("Authorization", "Bearer $FIRST_ATHLETE_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isForbidden
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.userIsNotACoach.type.toString())
    }

    @Test
    fun `update water activity - invalid water activity id`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val id = "invalid"

        val body =
            mapOf(
                "date" to DATE,
                "rpe" to 6,
                "condition" to "excellent",
                "trimp" to 130,
                "duration" to 60,
            )

        client.patch().uri("/water/$id")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidWaterActivityId.type.toString())
    }

    @Test
    fun `update water activity - not athletes coach`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "date" to DATE,
                "rpe" to 6,
                "condition" to "excellent",
                "trimp" to 130,
                "duration" to 60,
            )

        client.patch().uri("/water/$FIRST_ACTIVITY_ID")
            .header("Authorization", "Bearer $SECOND_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isForbidden
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.notAthletesCoach.type.toString())
    }

    @Test
    fun `update water activity - not found`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "date" to DATE,
                "rpe" to 6,
                "condition" to "excellent",
                "trimp" to 130,
                "duration" to 60,
            )

        client.patch().uri("/water/0")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isNotFound
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.waterActivityNotFound.type.toString())
    }

    @Test
    fun `update water activity - not water activity`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "date" to DATE,
                "rpe" to 6,
                "condition" to "excellent",
                "trimp" to 130,
                "duration" to 60,
            )

        client.patch().uri("/water/$NOT_WATER_ACTIVITY_ID")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.notWaterActivity.type.toString())
    }

    @Test
    fun `update water activity - invalid date`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "date" to "invalid-date",
                "rpe" to 6,
                "condition" to "excellent",
                "trimp" to 130,
                "duration" to 60,
            )

        client.patch().uri("/water/$FIRST_ACTIVITY_ID")
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
    fun `update water activity - invalid rpe`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "date" to DATE,
                "rpe" to -1,
                "condition" to "excellent",
                "trimp" to 130,
                "duration" to 60,
            )

        client.patch().uri("/water/$FIRST_ACTIVITY_ID")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidRpe.type.toString())
    }

    @Test
    fun `update water activity - invalid trimp`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "date" to DATE,
                "rpe" to 6,
                "condition" to "excellent",
                "trimp" to -1,
                "duration" to 60,
            )

        client.patch().uri("/water/$FIRST_ACTIVITY_ID")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidTrimp.type.toString())
    }

    @Test
    fun `update water activity - invalid duration`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "date" to DATE,
                "rpe" to 6,
                "condition" to "excellent",
                "trimp" to 130,
                "duration" to -1,
            )

        client.patch().uri("/water/$FIRST_ACTIVITY_ID")
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
    fun `update water activity - invalid wave order`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "date" to DATE,
                "rpe" to 6,
                "condition" to "excellent",
                "trimp" to 130,
                "duration" to 60,
                "waves" to
                    listOf(
                        mapOf(
                            "points" to 10.0,
                            "rightSide" to true,
                            "order" to -1,
                            "maneuvers" to
                                listOf(
                                    mapOf(
                                        "waterManeuverId" to 1,
                                        "rightSide" to true,
                                        "success" to true,
                                    ),
                                ),
                        ),
                    ),
            )

        client.patch().uri("/water/$FIRST_ACTIVITY_ID")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidWaveOrder.type.toString())
    }

    @Test
    fun `update water activity - invalid water maneuver`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "date" to DATE,
                "rpe" to 6,
                "condition" to "excellent",
                "trimp" to 130,
                "duration" to 60,
                "waves" to
                    listOf(
                        mapOf(
                            "points" to 10.0,
                            "rightSide" to true,
                            "order" to 3,
                            "maneuvers" to
                                listOf(
                                    mapOf(
                                        "waterManeuverId" to 0, // Invalid water maneuver ID
                                        "rightSide" to true,
                                        "success" to true,
                                    ),
                                ),
                        ),
                    ),
            )

        client.patch().uri("/water/$FIRST_ACTIVITY_ID")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidWaterManeuver.type.toString())
    }

    @Test
    fun `update water activity - invalid maneuvers`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "date" to DATE,
                "rpe" to 6,
                "condition" to "excellent",
                "trimp" to 130,
                "duration" to 60,
                "waves" to
                    listOf(
                        mapOf(
                            "points" to 10.0,
                            "rightSide" to true,
                            "order" to 3,
                        ),
                    ),
            )

        client.patch().uri("/water/$FIRST_ACTIVITY_ID")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidManeuvers.type.toString())
    }

    @Test
    fun `update water activity - invalid right side`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "date" to DATE,
                "rpe" to 6,
                "condition" to "excellent",
                "trimp" to 130,
                "duration" to 60,
                "waves" to
                    listOf(
                        mapOf(
                            "points" to 10.0,
                            "rightSide" to null,
                            "order" to 4,
                            "maneuvers" to
                                listOf(
                                    mapOf(
                                        "waterManeuverId" to 1,
                                        "rightSide" to true,
                                        "success" to true,
                                    ),
                                ),
                        ),
                    ),
            )

        client.patch().uri("/water/$FIRST_ACTIVITY_ID")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidRightSide.type.toString())
    }

    @Test
    fun `update water activity - invalid success`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "date" to DATE,
                "rpe" to 6,
                "condition" to "excellent",
                "trimp" to 130,
                "duration" to 60,
                "waves" to
                    listOf(
                        mapOf(
                            "points" to 10.0,
                            "rightSide" to true,
                            "order" to 5,
                            "maneuvers" to
                                listOf(
                                    mapOf(
                                        "waterManeuverId" to 1,
                                        "rightSide" to true,
                                        "success" to null,
                                    ),
                                ),
                        ),
                    ),
            )

        client.patch().uri("/water/$FIRST_ACTIVITY_ID")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidSuccess.type.toString())
    }

    @Test
    fun `update water activity - maneuver not found`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "date" to DATE,
                "rpe" to 6,
                "condition" to "excellent",
                "trimp" to 130,
                "duration" to 60,
                "waves" to
                    listOf(
                        mapOf(
                            "id" to 2,
                            "points" to 10.0,
                            "rightSide" to true,
                            "order" to 4,
                            "maneuvers" to
                                listOf(
                                    mapOf(
                                        "id" to 0,
                                        "waterManeuverId" to 1,
                                        "rightSide" to true,
                                        "success" to true,
                                    ),
                                ),
                        ),
                    ),
            )

        client.patch().uri("/water/$FIRST_ACTIVITY_ID")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isNotFound
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.maneuverNotFound.type.toString())
    }

    @Test
    fun `update water activity - wave not found`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "date" to DATE,
                "rpe" to 6,
                "condition" to "excellent",
                "trimp" to 130,
                "duration" to 60,
                "waves" to
                    listOf(
                        mapOf(
                            "id" to 0,
                            "points" to 10.0,
                            "rightSide" to true,
                            "order" to 4,
                            "maneuvers" to
                                listOf(
                                    mapOf(
                                        "waterManeuverId" to 1,
                                        "rightSide" to true,
                                        "success" to true,
                                    ),
                                ),
                        ),
                    ),
            )

        client.patch().uri("/water/$FIRST_ACTIVITY_ID")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isNotFound
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.waveNotFound.type.toString())
    }

    /**
     * Remove Water Activity Test
     */

    @Test
    fun `remove water activity - success`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.delete().uri("/water/$WATER_ACTIVITY_TO_REMOVE")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .exchange()
            .expectStatus().isNoContent
    }

    @Test
    fun `remove water activity - unauthorized`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.delete().uri("/water/$FIRST_ACTIVITY_ID")
            .exchange()
            .expectStatus().isUnauthorized
    }

    @Test
    fun `remove water activity - not athletes coach`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.delete().uri("/water/$SECOND_ACTIVITY_ID")
            .header("Authorization", "Bearer $SECOND_COACH_TOKEN")
            .exchange()
            .expectStatus().isForbidden
    }

    @Test
    fun `remove water activity - not found`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.delete().uri("/water/0")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .exchange()
            .expectStatus().isNotFound
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.waterActivityNotFound.type.toString())
    }

    @Test
    fun `remove water activity - not water activity`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.delete().uri("/water/$NOT_WATER_ACTIVITY_ID")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.notWaterActivity.type.toString())
    }

    @Test
    fun `remove water activity - invalid id`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.delete().uri("/water/invalid-id")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidWaterActivityId.type.toString())
    }

    @Test
    fun `remove water activity - user is not a coach`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.delete().uri("/water/invalid-id")
            .header("Authorization", "Bearer $FIRST_ATHLETE_TOKEN")
            .exchange()
            .expectStatus().isForbidden
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.userIsNotACoach.type.toString())
    }

    /**
     * Create Questionnaire Test
     */

    @Test
    fun `create questionnaire - success (coach)`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "sleep" to 5,
                "fatigue" to 3,
                "stress" to 2,
                "musclePain" to 1,
            )

        client.post().uri("/water/$FIRST_ACTIVITY_ID/questionnaire")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isNoContent
    }

    @Test
    fun `create questionnaire - success (athlete)`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "sleep" to 5,
                "fatigue" to 3,
                "stress" to 2,
                "musclePain" to 1,
            )

        client.post().uri("/water/$SOME_ACTIVITY_ID/questionnaire")
            .header("Authorization", "Bearer $FIRST_ATHLETE_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isNoContent
    }

    @Test
    fun `create questionnaire - unauthorized`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "sleep" to 7,
                "fatigue" to 3,
                "stress" to 2,
                "musclePain" to 1,
            )

        client.post().uri("/water/$FIRST_ACTIVITY_ID/questionnaire")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isUnauthorized
    }

    @Test
    fun `create questionnaire - invalid water activity id`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val id = "invalid"

        val body =
            mapOf(
                "sleep" to 7,
                "fatigue" to 3,
                "stress" to 2,
                "musclePain" to 1,
            )

        client.post().uri("/water/$id/questionnaire")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidWaterActivityId.type.toString())
    }

    @Test
    fun `create questionnaire - already exists`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "sleep" to 7,
                "fatigue" to 3,
                "stress" to 2,
                "musclePain" to 1,
            )

        client.post().uri("/water/$SECOND_ACTIVITY_ID/questionnaire")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isEqualTo(409)
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.questionnaireAlreadyExists.type.toString())
    }

    @Test
    fun `create questionnaire - water activity not found`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "sleep" to 7,
                "fatigue" to 3,
                "stress" to 2,
                "musclePain" to 1,
            )

        val id = Random.nextInt()

        client.post().uri("/water/$id/questionnaire")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isNotFound
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.waterActivityNotFound.type.toString())
    }

    @Test
    fun `create questionnaire - not athletes coach`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "sleep" to 7,
                "fatigue" to 3,
                "stress" to 2,
                "musclePain" to 1,
            )

        client.post().uri("/water/$FIRST_ACTIVITY_ID/questionnaire")
            .header("Authorization", "Bearer $SECOND_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isForbidden
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.notAthletesCoach.type.toString())
    }

    @Test
    fun `create questionnaire - invalid sleep value`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "sleep" to -1,
                "fatigue" to 3,
                "stress" to 2,
                "musclePain" to 1,
            )

        client.post().uri("/water/$THIRD_ACTIVITY_ID/questionnaire")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidSleep.type.toString())
    }

    @Test
    fun `create questionnaire - invalid fatigue value`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "sleep" to 5,
                "fatigue" to -1,
                "stress" to 2,
                "musclePain" to 1,
            )

        client.post().uri("/water/$THIRD_ACTIVITY_ID/questionnaire")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidFatigue.type.toString())
    }

    @Test
    fun `create questionnaire - invalid stress value`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "sleep" to 5,
                "fatigue" to 3,
                "stress" to -1,
                "musclePain" to 1,
            )

        client.post().uri("/water/$THIRD_ACTIVITY_ID/questionnaire")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidStress.type.toString())
    }

    @Test
    fun `create questionnaire - invalid muscle pain value`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "sleep" to 5,
                "fatigue" to 3,
                "stress" to 2,
                "musclePain" to -1,
            )

        client.post().uri("/water/$THIRD_ACTIVITY_ID/questionnaire")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidMusclePain.type.toString())
    }

    /**
     * Get Questionnaire Test
     */

    @Test
    fun `get questionnaire - success (coach)`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get().uri("/water/$SECOND_ACTIVITY_ID/questionnaire")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("sleep").exists()
            .jsonPath("fatigue").exists()
            .jsonPath("stress").exists()
            .jsonPath("musclePain").exists()
    }

    @Test
    fun `get questionnaire - success (athlete)`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get().uri("/water/$SECOND_ACTIVITY_ID/questionnaire")
            .header("Authorization", "Bearer $FIRST_ATHLETE_TOKEN")
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("sleep").exists()
            .jsonPath("fatigue").exists()
            .jsonPath("stress").exists()
            .jsonPath("musclePain").exists()
    }

    @Test
    fun `get questionnaire - unauthorized`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get().uri("/water/$FIRST_ACTIVITY_ID/questionnaire")
            .exchange()
            .expectStatus().isUnauthorized
    }

    @Test
    fun `get questionnaire - invalid water activity id`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val id = "invalid"

        client.get().uri("/water/$id/questionnaire")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidWaterActivityId.type.toString())
    }

    @Test
    fun `get questionnaire - activity not found`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val id = 0

        client.get().uri("/water/$id/questionnaire")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .exchange()
            .expectStatus().isNotFound
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.waterActivityNotFound.type.toString())
    }

    @Test
    fun `get questionnaire - questionnaire not found`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get().uri("/water/$THIRD_ACTIVITY_ID/questionnaire")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .exchange()
            .expectStatus().isNotFound
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.questionnaireNotFound.type.toString())
    }

    @Test
    fun `get questionnaire - not athletes coach`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get().uri("/water/$FIRST_ACTIVITY_ID/questionnaire")
            .header("Authorization", "Bearer $SECOND_COACH_TOKEN")
            .exchange()
            .expectStatus().isForbidden
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.notAthletesCoach.type.toString())
    }

    /**
     * Remove Questionnaire Test
     */

    companion object {
        private const val FIRST_COACH_TOKEN = "i_aY-4lpMqAIMuhkimTbKy4xYEuyvgFPaaTpVS0lctQ="
        private const val FIRST_COACH_ID = 1
        private const val SECOND_COACH_TOKEN = "fM5JjtPOUqtnZg1lB7jnJhXBP5gI2WbIIBoO3JhYM5M="
        private const val FIRST_ATHLETE_ID = 3
        private const val FIRST_ATHLETE_TOKEN = "0FaEBvcKLwE1YKrLYdhHd5p61EQtJThf3mEX6o28Lgo="

        private const val FIRST_ACTIVITY_ID = 2
        private const val SECOND_ACTIVITY_ID = 3
        private const val THIRD_ACTIVITY_ID = 5
        private const val NOT_WATER_ACTIVITY_ID = 1
        private const val WATER_ACTIVITY_TO_REMOVE = 6
        private const val SOME_ACTIVITY_ID = 17
        private const val SECOND_WAVE_ID = 2
        private const val THIRD_WAVE_ID = 3

        private const val DATE = "03-05-2025" // date long = 1736006400000
        private const val ACTIVITY_DATE = 1746144000000
        private const val DATE_WITHOUT_MICROCYCLE = "01-01-2000" // date long = 1736006400000

        private fun randomString() = "String_${abs(Random.nextLong())}"
    }
}

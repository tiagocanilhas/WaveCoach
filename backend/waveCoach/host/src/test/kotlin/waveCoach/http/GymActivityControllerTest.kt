package waveCoach.http

import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import waveCoach.host.WaveCoachApplication
import waveCoach.http.model.output.Problem
import kotlin.test.Test

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [WaveCoachApplication::class])
class GymActivityControllerTest {
    @LocalServerPort
    var port: Int = 0

    val BASE_URL: String
        get() = "http://localhost:$port/api"


    /**
     * create gym activity tests
     */

    @Test
    fun `create gym activity - success`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "athleteId" to THIRD_ATHLETE_ID,
                "date" to VALID_DATE,
                "exercises" to listOf(
                    mapOf(
                        "sets" to listOf(
                            mapOf(
                                "reps" to 10,
                                "weight" to 100.0,
                                "rest" to 60.0,
                            ),
                            mapOf(
                                "reps" to 20,
                                "weight" to 100.0,
                                "rest" to 60.0,
                            ),
                        ),
                        "gymExerciseId" to 1,
                    ),
                    mapOf(
                        "sets" to listOf(
                            mapOf(
                                "reps" to 10,
                                "weight" to 100.0,
                                "rest" to 60.0,
                            ),
                            mapOf(
                                "reps" to 20,
                                "weight" to 100.0,
                                "rest" to 60.0,
                            ),
                        ),
                        "gymExerciseId" to 2,
                    ),
                ),
            )

        client.post().uri("/gym")
            .header("Authorization", "Bearer $SECOND_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isCreated
            .expectHeader().exists("Location")
            .expectHeader().value("location") {
                assertTrue(it.startsWith("/api/gym"))
            }
    }

    @Test
    fun `create gym activity - unauthorized`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "athleteId" to THIRD_ATHLETE_ID,
                "date" to VALID_DATE,
                "exercises" to listOf(
                    mapOf(
                        "sets" to listOf(
                            mapOf(
                                "reps" to 10,
                                "weight" to 100.0,
                                "rest" to 60.0,
                            ),
                            mapOf(
                                "reps" to 20,
                                "weight" to 100.0,
                                "rest" to 60.0,
                            ),
                        ),
                        "gymExerciseId" to 1,
                    ),
                    mapOf(
                        "sets" to listOf(
                            mapOf(
                                "reps" to 10,
                                "weight" to 100.0,
                                "rest" to 60.0,
                            ),
                            mapOf(
                                "reps" to 20,
                                "weight" to 100.0,
                                "rest" to 60.0,
                            ),
                        ),
                        "gymExerciseId" to 2,
                    ),
                ),
            )

        client.post().uri("/gym")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isUnauthorized
    }

    @Test
    fun `create gym activity - invalid date`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "athleteId" to THIRD_ATHLETE_ID,
                "date" to INVALID_DATE,
                "exercises" to listOf(
                    mapOf(
                        "sets" to listOf(
                            mapOf(
                                "reps" to 10,
                                "weight" to 100.0,
                                "rest" to 60.0,
                            ),
                            mapOf(
                                "reps" to 20,
                                "weight" to 100.0,
                                "rest" to 60.0,
                            ),
                        ),
                        "gymExerciseId" to 1,
                    ),
                    mapOf(
                        "sets" to listOf(
                            mapOf(
                                "reps" to 10,
                                "weight" to 100.0,
                                "rest" to 60.0,
                            ),
                            mapOf(
                                "reps" to 20,
                                "weight" to 100.0,
                                "rest" to 60.0,
                            ),
                        ),
                        "gymExerciseId" to 2,
                    ),
                ),
            )

        client.post().uri("/gym")
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
    fun `create gym activity - athlete not found`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val id = 0

        val body =
            mapOf(
                "athleteId" to id,
                "date" to VALID_DATE,
                "exercises" to listOf(
                    mapOf(
                        "sets" to listOf(
                            mapOf(
                                "reps" to 10,
                                "weight" to 100.0,
                                "rest" to 60.0,
                            ),
                            mapOf(
                                "reps" to 20,
                                "weight" to 100.0,
                                "rest" to 60.0,
                            ),
                        ),
                        "gymExerciseId" to 1,
                    ),
                    mapOf(
                        "sets" to listOf(
                            mapOf(
                                "reps" to 10,
                                "weight" to 100.0,
                                "rest" to 60.0,
                            ),
                            mapOf(
                                "reps" to 20,
                                "weight" to 100.0,
                                "rest" to 60.0,
                            ),
                        ),
                        "gymExerciseId" to 2,
                    ),
                ),
            )

        client.post().uri("/gym")
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
    fun `create gym activity - not athlete's coach`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "athleteId" to THIRD_ATHLETE_ID,
                "date" to VALID_DATE,
                "exercises" to listOf(
                    mapOf(
                        "sets" to listOf(
                            mapOf(
                                "reps" to 10,
                                "weight" to 100.0,
                                "rest" to 60.0,
                            ),
                            mapOf(
                                "reps" to 20,
                                "weight" to 100.0,
                                "rest" to 60.0,
                            ),
                        ),
                        "gymExerciseId" to 1,
                    ),
                    mapOf(
                        "sets" to listOf(
                            mapOf(
                                "reps" to 10,
                                "weight" to 100.0,
                                "rest" to 60.0,
                            ),
                            mapOf(
                                "reps" to 20,
                                "weight" to 100.0,
                                "rest" to 60.0,
                            ),
                        ),
                        "gymExerciseId" to 2,
                    ),
                ),
            )

        client.post().uri("/gym")
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
    fun `create gym activity - invalid gym exercise`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "athleteId" to THIRD_ATHLETE_ID,
                "date" to VALID_DATE,
                "exercises" to listOf(
                    mapOf(
                        "sets" to listOf(
                            mapOf(
                                "reps" to 10,
                                "weight" to 100.0,
                                "rest" to 60.0,
                            ),
                            mapOf(
                                "reps" to 20,
                                "weight" to 100.0,
                                "rest" to 60.0,
                            ),
                        ),
                        "gymExerciseId" to -1,
                    ),
                ),
            )

        client.post().uri("/gym")
            .header("Authorization", "Bearer $SECOND_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidGymExercise.type.toString())
    }

    @Test
    fun `create gym activity - invalid set`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "athleteId" to THIRD_ATHLETE_ID,
                "date" to VALID_DATE,
                "exercises" to listOf(
                    mapOf(
                        "sets" to listOf(
                            mapOf(
                                "reps" to -1,
                                "weight" to 100.0,
                                "rest" to 60.0,
                            ),
                        ),
                        "gymExerciseId" to 1,
                    ),
                ),
            )

        client.post().uri("/gym")
            .header("Authorization", "Bearer $SECOND_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidSets.type.toString())
    }

    @Test
    fun `create gym activity - user is not a coach`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "athleteId" to THIRD_ATHLETE_ID,
                "date" to VALID_DATE,
                "exercises" to listOf(
                    mapOf(
                        "sets" to listOf(
                            mapOf(
                                "reps" to 10,
                                "weight" to 100.0,
                                "rest" to 60.0,
                            ),
                            mapOf(
                                "reps" to 20,
                                "weight" to 100.0,
                                "rest" to 60.0,
                            ),
                        ),
                        "gymExerciseId" to 1,
                    ),
                    mapOf(
                        "sets" to listOf(
                            mapOf(
                                "reps" to 10,
                                "weight" to 100.0,
                                "rest" to 60.0,
                            ),
                            mapOf(
                                "reps" to 20,
                                "weight" to 100.0,
                                "rest" to 60.0,
                            ),
                        ),
                        "gymExerciseId" to 2,
                    ),
                ),
            )

        client.post().uri("/gym")
            .header("Authorization", "Bearer $FIRST_ATHLETE_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isForbidden
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.userIsNotACoach.type.toString())
    }



    /**
     * Get gym activity tests
     */

    @Test
    fun `get gym activity - success (coach)`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get().uri("/gym/$FIRST_GYM_ACTIVITY_ID")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("id").isEqualTo(FIRST_GYM_ACTIVITY_ID)
            .jsonPath("date").isEqualTo(FIRST_GYM_ACTIVITY_DATE)
            .jsonPath("exercises").exists()
    }

    @Test
    fun `get gym activity - success (athlete)`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get().uri("/gym/$FIRST_GYM_ACTIVITY_ID")
            .header("Authorization", "Bearer $FIRST_ATHLETE_TOKEN")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("id").isEqualTo(FIRST_GYM_ACTIVITY_ID)
            .jsonPath("date").isEqualTo(FIRST_GYM_ACTIVITY_DATE)
            .jsonPath("exercises").exists()
    }

    @Test
    fun `get gym activity - unauthorized`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get().uri("/gym/$FIRST_GYM_ACTIVITY_ID")
            .exchange()
            .expectStatus().isUnauthorized
    }

    @Test
    fun `get gym activity - invalid gym activity id`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val id = "invalid"

        client.get().uri("/gym/$id")
            .header("Authorization", "Bearer $SECOND_COACH_TOKEN")
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidGymActivityId.type.toString())
    }

    @Test
    fun `get gym activity - not athlete's coach`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get().uri("/gym/$SECOND_GYM_ACTIVITY_ID")
            .header("Authorization", "Bearer $SECOND_COACH_TOKEN")
            .exchange()
            .expectStatus().isForbidden
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.notAthletesCoach.type.toString())
    }

    @Test
    fun `get gym activity - gym activity not found`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val id = 0

        client.get().uri("/gym/$id")
            .header("Authorization", "Bearer $SECOND_COACH_TOKEN")
            .exchange()
            .expectStatus().isNotFound
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.gymActivityNotFound.type.toString())
    }

    @Test
    fun `get gym activity - not gym activity`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get().uri("/gym/$NOT_GYM_ACTIVITY_ID")
            .header("Authorization", "Bearer $SECOND_COACH_TOKEN")
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.notGymActivity.type.toString())
    }



    /**
     * Remove gym activity tests
     */

    @Test
    fun `remove gym activity - success`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.delete().uri("/gym/$FIRST_GYM_ACTIVITY_ID")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .exchange()
            .expectStatus().isNoContent
    }

    @Test
    fun `remove gym activity - unauthorized`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.delete().uri("/gym/$FIRST_GYM_ACTIVITY_ID")
            .exchange()
            .expectStatus().isUnauthorized
    }

    @Test
    fun `remove gym activity - invalid gym activity id`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val id = "invalid"

        client.delete().uri("/gym/$id")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidGymActivityId.type.toString())
    }

    @Test
    fun `remove gym activity - not athlete's coach`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.delete().uri("/gym/$FIRST_GYM_ACTIVITY_ID")
            .header("Authorization", "Bearer $SECOND_COACH_TOKEN")
            .exchange()
            .expectStatus().isForbidden
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.notAthletesCoach.type.toString())
    }

    @Test
    fun `remove gym activity - gym activity not found`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val id = 0

        client.delete().uri("/gym/$id")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .exchange()
            .expectStatus().isNotFound
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.gymActivityNotFound.type.toString())
    }

    @Test
    fun `remove gym activity - user is not a coach`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.delete().uri("/gym/$FIRST_GYM_ACTIVITY_ID")
            .header("Authorization", "Bearer $FIRST_ATHLETE_TOKEN")
            .exchange()
            .expectStatus().isForbidden
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.userIsNotACoach.type.toString())
    }

    @Test
    fun `remove gym activity - not gym activity`(){
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.delete().uri("/gym/$NOT_GYM_ACTIVITY_ID")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.notGymActivity.type.toString())
    }

    companion object {
        private const val VALID_DATE = "18-07-2025"
        private const val INVALID_DATE = "32-01-2000"

        private const val FIRST_COACH_TOKEN = "i_aY-4lpMqAIMuhkimTbKy4xYEuyvgFPaaTpVS0lctQ="

        private const val SECOND_COACH_TOKEN = "fM5JjtPOUqtnZg1lB7jnJhXBP5gI2WbIIBoO3JhYM5M="

        private const val FIRST_ATHLETE_ID = 3
        private const val FIRST_ATHLETE_TOKEN = "0FaEBvcKLwE1YKrLYdhHd5p61EQtJThf3mEX6o28Lgo="

        private const val THIRD_ATHLETE_ID = 5

        private const val FIRST_GYM_ACTIVITY_ID = 1
        private const val FIRST_GYM_ACTIVITY_DATE = 1746057600000
        private const val SECOND_GYM_ACTIVITY_ID = 4

        private const val NOT_GYM_ACTIVITY_ID = 2

    }
}
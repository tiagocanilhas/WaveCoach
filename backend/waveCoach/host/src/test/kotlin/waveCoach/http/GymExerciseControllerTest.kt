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
class GymExerciseControllerTest {
    @LocalServerPort
    var port: Int = 0

    val BASE_URL: String
        get() = "http://localhost:$port/api"

    /**
     * Create GymExercise Test
     */

    @Test
    fun `create gym exercise - success`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val input = mapOf(
            "name" to randomString(),
            "category" to "chest",
        )

        val body = MultipartBodyBuilder().apply {
            part("input", input)
        }.build()

        client.post()
            .uri("/gym/exercise")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .bodyValue(body)
            .exchange()
            .expectStatus().isCreated
            .expectHeader().exists("Location")
            .expectHeader().value("location") {
                assertTrue(it.startsWith("/api/gym/exercise/"))
            }
    }

    @Test
    fun `create gym exercise with invalid name`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val input = mapOf(
            "name" to "",
            "category" to "chest",
        )

        val body = MultipartBodyBuilder().apply {
            part("input", input)
        }.build()

        client.post()
            .uri("/gym/exercise")
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
    fun `create gym exercise with invalid category`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val input = mapOf(
            "name" to "Bench Press",
            "category" to "",
        )

        val body = MultipartBodyBuilder().apply {
            part("input", input)
        }.build()

        client.post()
            .uri("/gym/exercise")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .bodyValue(body)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidCategory.type.toString())
    }

    @Test
    fun `create gym exercise with already existing name`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val input = mapOf(
            "name" to "Shoulder Press",
            "category" to "shoulders",
        )

        val body = MultipartBodyBuilder().apply {
            part("input", input)
        }.build()

        client.post()
            .uri("/gym/exercise")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .bodyValue(body)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.nameAlreadyExists.type.toString())
    }

    /**
     * Get all GymExercise Test
     */

    @Test
    fun `get all gym exercises`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get()
            .uri("/gym/exercise")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("gymExercises").exists()
            .jsonPath("gymExercises.length()").value<Int> { assertTrue(it > 0) }
    }

    /**
     * Update GymExercise Test
     */

    @Test
    fun `update gym exercise - success`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body = mapOf(
            "name" to "Push Up",
            "category" to "Chest",
        )

        client.put()
            .uri("/gym/exercise/$FIRST_GYM_EXERCISE_ID")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isNoContent
    }

    @Test
    fun `update gym exercise not found`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body = mapOf(
            "name" to "Push Up",
            "category" to "Chest",
        )

        client.put()
            .uri("/gym/exercise/0")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isNotFound
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.gymExerciseNotFound.type.toString())
    }

    @Test
    fun `update gym exercise with invalid name`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body = mapOf(
            "name" to "",
            "category" to "Chest",
        )

        client.put()
            .uri("/gym/exercise/$FIRST_GYM_EXERCISE_ID")
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
    fun `update gym exercise with invalid category`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body = mapOf(
            "name" to "Push Up",
            "category" to "",
        )

        client.put()
            .uri("/gym/exercise/$FIRST_GYM_EXERCISE_ID")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidCategory.type.toString())
    }

    @Test
    fun `update gym exercise with already existing name`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body = mapOf(
            "name" to "Deadlift",
            "category" to "Back",
        )

        client.put()
            .uri("/gym/exercise/$FIRST_GYM_EXERCISE_ID")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.nameAlreadyExists.type.toString())
    }

    /**
     * Remove GymExercise Test
     */

    @Test
    fun `remove gym exercise - success`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.delete()
            .uri("/gym/exercise/$THIRD_GYM_EXERCISE_ID")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .exchange()
            .expectStatus().isNoContent
    }

    @Test
    fun `remove gym exercise not found`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.delete()
            .uri("/gym/exercise/0")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .exchange()
            .expectStatus().isNotFound
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.gymExerciseNotFound.type.toString())
    }

    companion object {
        private const val FIRST_COACH_TOKEN = "i_aY-4lpMqAIMuhkimTbKy4xYEuyvgFPaaTpVS0lctQ="
        private const val FIRST_COACH_ID = 1

        private const val FIRST_GYM_EXERCISE_ID = 1
        private const val THIRD_GYM_EXERCISE_ID = 3

        private fun randomString() = "String_${abs(Random.nextLong())}"
    }
}
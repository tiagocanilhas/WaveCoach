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

        val input =
            mapOf(
                "name" to randomString(),
                "birthdate" to VALID_DATE,
            )

        val body =
            MultipartBodyBuilder().apply {
                part("input", input)
            }.build()

        client.post().uri("/athletes")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .bodyValue(body)
            .exchange()
            .expectStatus().isCreated
            .expectHeader().exists("Location")
            .expectHeader().value("location") {
                assertTrue(it.startsWith("/api/athletes/"))
            }
    }

    @Test
    fun `create an athlete - unauthorized`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val input =
            mapOf(
                "name" to randomString(),
                "birthdate" to VALID_DATE,
            )

        val body =
            MultipartBodyBuilder().apply {
                part("input", input)
            }.build()

        client.post().uri("/athletes")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .bodyValue(body)
            .exchange()
            .expectStatus().isUnauthorized
    }

    @Test
    fun `create an athlete - invalid birth date`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val input =
            mapOf(
                "name" to randomString(),
                "birthdate" to INVALID_DATE,
            )

        val body =
            MultipartBodyBuilder().apply {
                part("input", input)
            }.build()

        client.post().uri("/athletes")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .bodyValue(body)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidBirthdate.type.toString())
    }

    @Test
    fun `create an athlete - invalid name`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val invalidNames =
            listOf(
                "",
                "a".repeat(65),
            )

        invalidNames.forEach { name ->
            val input =
                mapOf(
                    "name" to name,
                    "birthdate" to VALID_DATE,
                )

            val body =
                MultipartBodyBuilder().apply {
                    part("input", input)
                }.build()

            client.post().uri("/athletes")
                .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(body)
                .exchange()
                .expectStatus().isBadRequest
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .jsonPath("type").isEqualTo(Problem.invalidName.type.toString())
        }
    }

    @Test
    fun `create an athlete - user is not coach`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val input =
            mapOf(
                "name" to randomString(),
                "birthdate" to VALID_DATE,
            )

        val body =
            MultipartBodyBuilder().apply {
                part("input", input)
            }.build()

        client.post().uri("/athletes")
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
     * Get Athlete Tests
     */

    @Test
    fun `get an athlete - success (coach)`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get().uri("/athletes/$FIRST_ATHLETE_ID")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("uid").isEqualTo(FIRST_ATHLETE_ID)
            .jsonPath("coach").isEqualTo(FIRST_COACH_ID)
            .jsonPath("name").isEqualTo(FIRST_ATHLETE_NAME)
            .jsonPath("birthdate").isEqualTo(FIRST_ATHLETE_BIRTH_DATE)
    }

    @Test
    fun `get an athlete - success (athlete)`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get().uri("/athletes/$FIRST_ATHLETE_ID")
            .header("Authorization", "Bearer $FIRST_ATHLETE_TOKEN")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("uid").isEqualTo(FIRST_ATHLETE_ID)
            .jsonPath("coach").isEqualTo(FIRST_COACH_ID)
            .jsonPath("name").isEqualTo(FIRST_ATHLETE_NAME)
            .jsonPath("birthdate").isEqualTo(FIRST_ATHLETE_BIRTH_DATE)
    }

    @Test
    fun `get an athlete - unauthorized`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get().uri("/athletes/$FIRST_ATHLETE_ID")
            .exchange()
            .expectStatus().isUnauthorized
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
    fun `get an athlete - not athlete's coach (coach)`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get().uri("/athletes/$FIRST_ATHLETE_ID")
            .header("Authorization", "Bearer $SECOND_COACH_TOKEN")
            .exchange()
            .expectStatus().isForbidden
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.notAthletesCoach.type.toString())
    }

    @Test
    fun `get an athlete - not athlete's coach (athlete)`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get().uri("/athletes/$FIRST_ATHLETE_ID")
            .header("Authorization", "Bearer $SECOND_ATHLETE_TOKEN")
            .exchange()
            .expectStatus().isForbidden
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.notAthletesCoach.type.toString())
    }

    /**
     * Get Athlete List Tests
     */

    @Test
    fun `get athlete list - success`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get().uri("/athletes")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("athletes").exists()
            .jsonPath("athletes.length()").value<Int> { assertTrue(it > 0) }
    }

    @Test
    fun `get athlete list - unauthorized`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get().uri("/athletes")
            .exchange()
            .expectStatus().isUnauthorized
    }

    @Test
    fun `get athlete list - user is not a coach`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get().uri("/athletes")
            .header("Authorization", "Bearer $FIRST_ATHLETE_TOKEN")
            .exchange()
            .expectStatus().isForbidden
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.userIsNotACoach.type.toString())
    }

    /**
     * Update Athlete Tests
     */

    @Test
    fun `update an athlete - success`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "name" to randomString(),
                "birthdate" to VALID_DATE,
            )

        client.put().uri("/athletes/$FIRST_ATHLETE_ID")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isNoContent
    }

    @Test
    fun `update an athlete - unauthorized`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "name" to randomString(),
                "birthdate" to VALID_DATE,
            )

        client.put().uri("/athletes/$FIRST_ATHLETE_ID")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isUnauthorized
    }

    @Test
    fun `update an athlete - invalid birth date`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "name" to randomString(),
                "birthdate" to INVALID_DATE,
            )

        client.put().uri("/athletes/$FIRST_ATHLETE_ID")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidBirthdate.type.toString())
    }

    @Test
    fun `update an athlete - invalid name`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val invalidNames =
            listOf(
                "",
                "a".repeat(65),
            )

        invalidNames.forEach { name ->
            val body =
                mapOf(
                    "name" to name,
                    "birthdate" to VALID_DATE,
                )

            client.put().uri("/athletes/$FIRST_ATHLETE_ID")
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

    @Test
    fun `update an athlete - invalid athlete id`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val id = "invalid"

        val body =
            mapOf(
                "name" to randomString(),
                "birthdate" to VALID_DATE,
            )

        client.put().uri("/athletes/$id")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidAthleteId.type.toString())
    }

    @Test
    fun `update an athlete - athlete not found`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val id = 0

        val body =
            mapOf(
                "name" to randomString(),
                "birthdate" to VALID_DATE,
            )

        client.put().uri("/athletes/$id")
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
    fun `update an athlete - not athlete's coach`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "name" to randomString(),
                "birthdate" to VALID_DATE,
            )

        client.put().uri("/athletes/$FIRST_ATHLETE_ID")
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
    fun `update an athlete - user is not a coach`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "name" to randomString(),
                "birthdate" to VALID_DATE,
            )

        client.put().uri("/athletes/$FIRST_ATHLETE_ID")
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
     * Remove Athlete Tests
     */

    @Test
    fun `remove an athlete - success`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.delete().uri("/athletes/$FIFTH_ATHLETE_ID")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .exchange()
            .expectStatus().isNoContent
    }

    @Test
    fun `remove an athlete - unauthorized`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.delete().uri("/athletes/$FIRST_ATHLETE_ID")
            .exchange()
            .expectStatus().isUnauthorized
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

    @Test
    fun `remove an athlete - user is not a coach`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.delete().uri("/athletes/$FIRST_ATHLETE_ID")
            .header("Authorization", "Bearer $FIRST_ATHLETE_TOKEN")
            .exchange()
            .expectStatus().isForbidden
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.userIsNotACoach.type.toString())
    }

    /**
     * Generate Code Tests
     */

    @Test
    fun `generate code - success`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.post().uri("/athletes/$THIRD_ATHLETE_ID/code")
            .header("Authorization", "Bearer $SECOND_COACH_TOKEN")
            .exchange()
            .expectStatus().isCreated
            .expectBody()
            .jsonPath("code").exists()
            .jsonPath("expirationDate").exists()
    }

    @Test
    fun `generate code - unauthorized`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.post().uri("/athletes/$THIRD_ATHLETE_ID/code")
            .exchange()
            .expectStatus().isUnauthorized
    }

    @Test
    fun `generate code - invalid athlete id`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val id = "invalid"

        client.post().uri("/athletes/$id/code")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidAthleteId.type.toString())
    }

    @Test
    fun `generate code - athlete not found`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val id = 0

        client.post().uri("/athletes/$id/code")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .exchange()
            .expectStatus().isNotFound
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.athleteNotFound.type.toString())
    }

    @Test
    fun `generate code - not athlete's coach`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.post().uri("/athletes/$FIRST_ATHLETE_ID/code")
            .header("Authorization", "Bearer $SECOND_COACH_TOKEN")
            .exchange()
            .expectStatus().isForbidden
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.notAthletesCoach.type.toString())
    }

    @Test
    fun `generate code - credentials already changed`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.post().uri("/athletes/$SECOND_ATHLETE_ID/code")
            .header("Authorization", "Bearer $SECOND_COACH_TOKEN")
            .exchange()
            .expectStatus().isEqualTo(409)
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.credentialsAlreadyChanged.type.toString())
    }

    @Test
    fun `generate code - user is not a coach`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.post().uri("/athletes/$SECOND_ATHLETE_ID/code")
            .header("Authorization", "Bearer $SECOND_ATHLETE_TOKEN")
            .exchange()
            .expectStatus().isForbidden
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.userIsNotACoach.type.toString())
    }

    /**
     * Get by Code Tests
     */

    @Test
    fun `get by code - success`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get().uri("/athletes/code/$FIRST_ATHLETE_CODE")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("username").isEqualTo(FIRST_ATHLETE_USERNAME)
    }

    @Test
    fun `get by code - invalid code`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val code = "invalid"

        client.get().uri("/athletes/code/$code")
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidCode.type.toString())
    }

    /**
     * Change credentials Tests
     */

    @Test
    fun `change credentials - success`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "code" to FIRST_ATHLETE_CODE,
                "username" to randomString(),
                "password" to randomString(),
            )

        client.post().uri("/athletes/credentials")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isNoContent
    }

    @Test
    fun `Invalid credentials - invalid username`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "code" to FIRST_ATHLETE_CODE,
                "username" to "",
                "password" to randomString(),
            )

        client.post().uri("/athletes/credentials")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidUsername.type.toString())
    }

    @Test
    fun `change credentials - insecure password`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "code" to FIRST_ATHLETE_CODE,
                "username" to randomString(),
                "password" to "",
            )

        client.post().uri("/athletes/credentials")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.insecurePassword.type.toString())
    }

    @Test
    fun `change credentials - invalid code`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "code" to "invalid",
                "username" to randomString(),
                "password" to randomString(),
            )

        client.post().uri("/athletes/credentials")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidCode.type.toString())
    }

    /**
     * Create Characteristics Tests
     */

    @Test
    fun `create characteristics - success`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "date" to VALID_DATE,
                "height" to 1,
                "weight" to 1.0,
                "calories" to 1,
                "bodyFat" to 1.0,
                "waistSize" to 1,
                "armSize" to 1,
                "thighSize" to 1,
                "tricepFat" to 1,
                "abdominalFat" to 1,
                "thighFat" to 1,
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
    fun `create characteristics - unauthorized`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "date" to VALID_DATE,
                "height" to 1,
                "weight" to 1.0,
                "calories" to 1,
                "bodyFat" to 1.0,
                "waistSize" to 1,
                "armSize" to 1,
                "thighSize" to 1,
                "tricepFat" to 1,
                "abdominalFat" to 1,
                "thighFat" to 1,
            )

        client.post().uri("/athletes/$SECOND_ATHLETE_ID/characteristics")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isUnauthorized
    }

    @Test
    fun `create characteristics - invalid date`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "date" to INVALID_DATE,
                "height" to 1,
                "weight" to 1.0,
                "calories" to 1,
                "bodyFat" to 1.0,
                "waistSize" to 1,
                "armSize" to 1,
                "thighSize" to 1,
                "tricepFat" to 1,
                "abdominalFat" to 1,
                "thighFat" to 1,
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

        val body =
            mapOf(
                "date" to VALID_DATE,
                "height" to 1,
                "weight" to 1.0,
                "calories" to 1,
                "bodyFat" to 1.0,
                "waistSize" to 1,
                "armSize" to 1,
                "thighSize" to 1,
                "tricepFat" to 1,
                "abdominalFat" to 1,
                "thighFat" to 1,
            )

        val invalidCharacteristics =
            listOf(
                body + ("height" to -1),
                body + ("weight" to -1.0),
                body + ("calories" to -1),
                body + ("bodyFat" to -1.0),
                body + ("waistSize" to -1),
                body + ("armSize" to -1),
                body + ("thighSize" to -1),
                body + ("tricepFat" to -1),
                body + ("abdomenFat" to -1),
                body + ("thighFat" to -1),
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

        val body =
            mapOf(
                "date" to VALID_DATE,
                "height" to 1,
                "weight" to 1.0,
                "calories" to 1,
                "bodyFat" to 1.0,
                "waistSize" to 1,
                "armSize" to 1,
                "thighSize" to 1,
                "tricepFat" to 1,
                "abdominalFat" to 1,
                "thighFat" to 1,
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

        val body =
            mapOf(
                "date" to VALID_DATE,
                "height" to 1,
                "weight" to 1.0,
                "calories" to 1,
                "bodyFat" to 1.0,
                "waistSize" to 1,
                "armSize" to 1,
                "thighSize" to 1,
                "tricepFat" to 1,
                "abdominalFat" to 1,
                "thighFat" to 1,
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

        val body =
            mapOf(
                "date" to VALID_DATE,
                "height" to 1,
                "weight" to 1.0,
                "calories" to 1,
                "bodyFat" to 1.0,
                "waistSize" to 1,
                "armSize" to 1,
                "thighSize" to 1,
                "tricepFat" to 1,
                "abdominalFat" to 1,
                "thighFat" to 1,
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

        val body =
            mapOf(
                "date" to ATHLETE_CHARACTERISTICS_FIRST_DATE,
                "height" to 1,
                "weight" to 1.0,
                "calories" to 1,
                "bodyFat" to 1.0,
                "waistSize" to 1,
                "armSize" to 1,
                "thighSize" to 1,
                "tricepFat" to 1,
                "abdominalFat" to 1,
                "thighFat" to 1,
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

    @Test
    fun `create characteristics - user is not a coach`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "date" to ATHLETE_CHARACTERISTICS_FIRST_DATE,
                "height" to 1,
                "weight" to 1.0,
                "calories" to 1,
                "bodyFat" to 1.0,
                "waistSize" to 1,
                "armSize" to 1,
                "thighSize" to 1,
                "tricepFat" to 1,
                "abdominalFat" to 1,
                "thighFat" to 1,
            )

        client.post().uri("/athletes/$FIRST_ATHLETE_ID/characteristics")
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
     * Get Characteristics Tests
     */

    @Test
    fun `get characteristics - success (coach)`() {
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
            .jsonPath("bmi").isEqualTo(ATHLETE_BMI)
            .jsonPath("calories").isEqualTo(ATHLETE_CALORIES)
            .jsonPath("bodyFat").isEqualTo(ATHLETE_BODY_FAT)
            .jsonPath("waistSize").isEqualTo(ATHLETE_WAIST_SIZE)
            .jsonPath("armSize").isEqualTo(ATHLETE_ARM_SIZE)
            .jsonPath("thighSize").isEqualTo(ATHLETE_THIGH_SIZE)
            .jsonPath("tricepFat").isEqualTo(ATHLETE_TRICEP_FAT)
            .jsonPath("abdomenFat").isEqualTo(ATHLETE_ABDOMEN_FAT)
            .jsonPath("thighFat").isEqualTo(ATHLETE_THIGH_FAT)
    }

    @Test
    fun `get characteristics - success (athlete)`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get().uri("/athletes/$SECOND_ATHLETE_ID/characteristics/$ATHLETE_CHARACTERISTICS_FIRST_DATE")
            .header("Authorization", "Bearer $SECOND_ATHLETE_TOKEN")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("date").isEqualTo(ATHLETE_CHARACTERISTICS_FIRST_DATE_LONG)
            .jsonPath("uid").isEqualTo(SECOND_ATHLETE_ID)
            .jsonPath("height").isEqualTo(ATHLETE_HEIGHT)
            .jsonPath("weight").isEqualTo(ATHLETE_WEIGHT)
            .jsonPath("bmi").isEqualTo(ATHLETE_BMI)
            .jsonPath("calories").isEqualTo(ATHLETE_CALORIES)
            .jsonPath("bodyFat").isEqualTo(ATHLETE_BODY_FAT)
            .jsonPath("waistSize").isEqualTo(ATHLETE_WAIST_SIZE)
            .jsonPath("armSize").isEqualTo(ATHLETE_ARM_SIZE)
            .jsonPath("thighSize").isEqualTo(ATHLETE_THIGH_SIZE)
            .jsonPath("tricepFat").isEqualTo(ATHLETE_TRICEP_FAT)
            .jsonPath("abdomenFat").isEqualTo(ATHLETE_ABDOMEN_FAT)
            .jsonPath("thighFat").isEqualTo(ATHLETE_THIGH_FAT)
    }

    @Test
    fun `get characteristics - unauthorized`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get().uri("/athletes/$SECOND_ATHLETE_ID/characteristics/$ATHLETE_CHARACTERISTICS_FIRST_DATE")
            .exchange()
            .expectStatus().isUnauthorized
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
    fun `get characteristics list - success (coach)`() {
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
    fun `get characteristics list - success (athlete)`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get().uri("/athletes/$SECOND_ATHLETE_ID/characteristics")
            .header("Authorization", "Bearer $SECOND_ATHLETE_TOKEN")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("characteristics").exists()
            .jsonPath("characteristics.length()").value<Int> { assertTrue(it > 0) }
    }

    @Test
    fun `get characteristics list - unauthorized`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get().uri("/athletes/$SECOND_ATHLETE_ID/characteristics")
            .exchange()
            .expectStatus().isUnauthorized
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

        val body =
            mapOf(
                "height" to 1,
                "weight" to 1.0,
                "calories" to 1,
                "bodyFat" to 1.0,
                "waistSize" to 1,
                "armSize" to 1,
                "thighSize" to 1,
                "tricepFat" to 1,
                "abdominalFat" to 1,
                "thighFat" to 1,
            )

        client.put().uri("/athletes/$SECOND_ATHLETE_ID/characteristics/$VALID_DATE")
            .header("Authorization", "Bearer $SECOND_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isNoContent
    }

    @Test
    fun `update characteristics - unauthorized`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "height" to 1,
                "weight" to 1.0,
                "calories" to 1,
                "bodyFat" to 1.0,
                "waistSize" to 1,
                "armSize" to 1,
                "thighSize" to 1,
                "tricepFat" to 1,
                "abdominalFat" to 1,
                "thighFat" to 1,
            )

        client.put().uri("/athletes/$SECOND_ATHLETE_ID/characteristics/$VALID_DATE")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isUnauthorized
    }

    @Test
    fun `update characteristics - invalid date`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "height" to 1,
                "weight" to 1.0,
                "calories" to 1,
                "bodyFat" to 1.0,
                "waistSize" to 1,
                "armSize" to 1,
                "thighSize" to 1,
                "tricepFat" to 1,
                "abdominalFat" to 1,
                "thighFat" to 1,
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

        val body =
            mapOf(
                "height" to 1,
                "weight" to 1.0,
                "calories" to 1,
                "bodyFat" to 1.0,
                "waistSize" to 1,
                "armSize" to 1,
                "thighSize" to 1,
                "tricepFat" to 1,
                "abdominalFat" to 1,
                "thighFat" to 1,
            )

        val invalidCharacteristics =
            listOf(
                body + ("height" to -1),
                body + ("weight" to -1.0),
                body + ("calories" to -1),
                body + ("bodyFat" to -1.0),
                body + ("waistSize" to -1),
                body + ("armSize" to -1),
                body + ("thighSize" to -1),
                body + ("tricepFat" to -1),
                body + ("abdomenFat" to -1),
                body + ("thighFat" to -1),
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

        val body =
            mapOf(
                "height" to 1,
                "weight" to 1.0,
                "calories" to 1,
                "bodyFat" to 1.0,
                "waistSize" to 1,
                "armSize" to 1,
                "thighSize" to 1,
                "tricepFat" to 1,
                "abdominalFat" to 1,
                "thighFat" to 1,
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

        val body =
            mapOf(
                "height" to 1,
                "weight" to 1.0,
                "calories" to 1,
                "bodyFat" to 1.0,
                "waistSize" to 1,
                "armSize" to 1,
                "thighSize" to 1,
                "tricepFat" to 1,
                "abdominalFat" to 1,
                "thighFat" to 1,
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

        val body =
            mapOf(
                "height" to 1,
                "weight" to 1.0,
                "calories" to 1,
                "bodyFat" to 1.0,
                "waistSize" to 1,
                "armSize" to 1,
                "thighSize" to 1,
                "tricepFat" to 1,
                "abdominalFat" to 1,
                "thighFat" to 1,
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

    @Test
    fun `update characteristics - user is not a coach`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "height" to 1,
                "weight" to 1.0,
                "calories" to 1,
                "bodyFat" to 1.0,
                "waistSize" to 1,
                "armSize" to 1,
                "thighSize" to 1,
                "tricepFat" to 1,
                "abdominalFat" to 1,
                "thighFat" to 1,
            )

        client.put().uri("/athletes/$SECOND_ATHLETE_ID/characteristics/$VALID_DATE")
            .header("Authorization", "Bearer $SECOND_ATHLETE_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isForbidden
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.userIsNotACoach.type.toString())
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
    fun `remove characteristics - unauthorized`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.delete().uri("/athletes/$SECOND_ATHLETE_ID/characteristics/$ATHLETE_CHARACTERISTICS_SECOND_DATE")
            .exchange()
            .expectStatus().isUnauthorized
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

    @Test
    fun `remove characteristics - user is not a coach`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.delete().uri("/athletes/$SECOND_ATHLETE_ID/characteristics/$ANOTHER_VALID_DATE")
            .header("Authorization", "Bearer $SECOND_ATHLETE_TOKEN")
            .exchange()
            .expectStatus().isForbidden
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.userIsNotACoach.type.toString())
    }

    /**
     * Create Calendar tests
     */

    // TODO: Implement create calendar tests

    /**
     * Get Calendar tests
     */

    @Test
    fun `get calendar - success (coach)`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get().uri("/athletes/$FIRST_ATHLETE_ID/calendar")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("mesocycles").exists()
            .jsonPath("mesocycles.length()").value<Int> { assertTrue(it > 0) }
    }

    @Test
    fun `get calendar - success (athlete)`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get().uri("/athletes/$FIRST_ATHLETE_ID/calendar")
            .header("Authorization", "Bearer $FIRST_ATHLETE_TOKEN")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("mesocycles").exists()
            .jsonPath("mesocycles.length()").value<Int> { assertTrue(it > 0) }
    }

    @Test
    fun `get calendar - unauthorized`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get().uri("/athletes/$FIRST_ATHLETE_ID/calendar")
            .exchange()
            .expectStatus().isUnauthorized
    }

    @Test
    fun `get calendar - athlete not found`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val id = 0

        client.get().uri("/athletes/$id/calendar")
            .header("Authorization", "Bearer $FIRST_ATHLETE_TOKEN")
            .exchange()
            .expectStatus().isNotFound
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.athleteNotFound.type.toString())
    }

    @Test
    fun `get calendar - not athlete's coach`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get().uri("/athletes/$SECOND_ATHLETE_ID/calendar")
            .header("Authorization", "Bearer $FIRST_ATHLETE_TOKEN")
            .exchange()
            .expectStatus().isForbidden
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.notAthletesCoach.type.toString())

        client.get().uri("/athletes/$SECOND_ATHLETE_ID/calendar")
            .header("Authorization", "Bearer $FIRST_ATHLETE_TOKEN")
            .exchange()
            .expectStatus().isForbidden
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.notAthletesCoach.type.toString())
    }

    /**
     * Get Water Activities
     */

    @Test
    fun `get water actviites - success (coach)`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get().uri("/athletes/$FIRST_ATHLETE_ID/water")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("mesocycles").exists()
            .jsonPath("mesocycles.length()").value<Int> { assertTrue(it > 0) }
    }

    @Test
    fun `get water actviites - success (athlete)`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get().uri("/athletes/$FIRST_ATHLETE_ID/water")
            .header("Authorization", "Bearer $FIRST_ATHLETE_TOKEN")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("mesocycles").exists()
            .jsonPath("mesocycles.length()").value<Int> { assertTrue(it > 0) }
    }

    @Test
    fun `get water activities - unauthorized`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get().uri("/athletes/$FIRST_ATHLETE_ID/water")
            .exchange()
            .expectStatus().isUnauthorized
    }

    @Test
    fun `get water activities - invalid athlete id`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val id = "invalid"

        client.get().uri("/athletes/$id/water")
            .header("Authorization", "Bearer $FIRST_ATHLETE_TOKEN")
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidAthleteId.type.toString())
    }

    @Test
    fun `get water activities - athlete not found`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val id = 0

        client.get().uri("/athletes/$id/water")
            .header("Authorization", "Bearer $FIRST_ATHLETE_TOKEN")
            .exchange()
            .expectStatus().isNotFound
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.athleteNotFound.type.toString())
    }

    @Test
    fun `get water activities - not athlete's coach`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get().uri("/athletes/$SECOND_ATHLETE_ID/water")
            .header("Authorization", "Bearer $FIRST_ATHLETE_TOKEN")
            .exchange()
            .expectStatus().isForbidden
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.notAthletesCoach.type.toString())
    }

    /**
     * Create Competition Tests
     */

    @Test
    fun `create competition - success`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "date" to DATE,
                "location" to randomString(),
                "place" to 1,
                "heats" to
                        listOf(
                            mapOf(
                                "score" to 2,
                                "waterActivity" to
                                        mapOf(
                                            "athleteId" to FIRST_ATHLETE_ID,
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
                            )
                        )
            )

        client.post().uri("/athletes/$FIRST_ATHLETE_ID/competition")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isCreated.expectStatus().isCreated
            .expectHeader().exists("Location")
            .expectHeader().value("location")
            {
                assertTrue(it.startsWith("/api/athletes/$FIRST_ATHLETE_ID/competition/"))
            }
    }

    @Test
    fun `create competition - unauthorized`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "date" to DATE,
                "location" to randomString(),
                "place" to 1,
                "heats" to
                        listOf(
                            mapOf(
                                "score" to 2,
                                "waterActivity" to
                                        mapOf(
                                            "athleteId" to FIRST_ATHLETE_ID,
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
                            )
                        )
            )

        client.post().uri("/athletes/$FIRST_ATHLETE_ID/competition")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isUnauthorized
    }

    @Test
    fun `create competition - invalid date`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "date" to INVALID_DATE,
                "location" to randomString(),
                "place" to 1,
                "heats" to
                        listOf(
                            mapOf(
                                "score" to 2,
                                "waterActivity" to
                                        mapOf(
                                            "athleteId" to FIRST_ATHLETE_ID,
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
                            )
                        )
            )

        client.post().uri("/athletes/$FIRST_ATHLETE_ID/competition")
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
    fun `create competition - invalid athlete id`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val id = "invalid"

        val body =
            mapOf(
                "date" to DATE,
                "location" to randomString(),
                "place" to 1,
                "heats" to
                        listOf(
                            mapOf(
                                "score" to 2,
                                "waterActivity" to
                                        mapOf(
                                            "athleteId" to FIRST_ATHLETE_ID,
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
                            )
                        )
            )

        client.post().uri("/athletes/$id/competition")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidAthleteId.type.toString())
    }

    @Test
    fun `create competition - athlete not found`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val id = 0

        val body =
            mapOf(
                "date" to DATE,
                "location" to randomString(),
                "place" to 1,
                "heats" to
                        listOf(
                            mapOf(
                                "score" to 2,
                                "waterActivity" to
                                        mapOf(
                                            "athleteId" to FIRST_ATHLETE_ID,
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
                            )
                        )
            )

        client.post().uri("/athletes/$id/competition")
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
    fun `create competition - not athlete's coach`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "date" to DATE,
                "location" to randomString(),
                "place" to 1,
                "heats" to
                        listOf(
                            mapOf(
                                "score" to 2,
                                "waterActivity" to
                                        mapOf(
                                            "athleteId" to SECOND_ATHLETE_ID,
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
                            )
                        )
            )

        client.post().uri("/athletes/$SECOND_ATHLETE_ID/competition")
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
    fun `create competition - user is not a coach`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "date" to DATE,
                "location" to randomString(),
                "place" to 1,
                "heats" to
                        listOf(
                            mapOf(
                                "score" to 2,
                                "waterActivity" to
                                        mapOf(
                                            "athleteId" to FIRST_ATHLETE_ID,
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
                            )
                        )
            )

        client.post().uri("/athletes/$FIRST_ATHLETE_ID/competition")
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
    fun `create competition - activity without microcycle`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "date" to VALID_DATE,
                "location" to randomString(),
                "place" to 1,
                "heats" to
                        listOf(
                            mapOf(
                                "score" to 2,
                                "waterActivity" to
                                        mapOf(
                                            "athleteId" to FIRST_ATHLETE_ID,
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
                            )
                        )
            )

        client.post().uri("/athletes/$FIRST_ATHLETE_ID/competition")
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
    fun `create competition - invalid duration`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "date" to DATE,
                "location" to randomString(),
                "place" to 1,
                "heats" to
                        listOf(
                            mapOf(
                                "score" to 2,
                                "waterActivity" to
                                        mapOf(
                                            "athleteId" to FIRST_ATHLETE_ID,
                                            "rpe" to 5,
                                            "condition" to "good",
                                            "trimp" to 120,
                                            "duration" to -60, // Invalid duration
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
                            )
                        )
            )

        client.post().uri("/athletes/$FIRST_ATHLETE_ID/competition")
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
    fun `create competition - invalid rpe`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "date" to DATE,
                "location" to randomString(),
                "place" to 1,
                "heats" to
                        listOf(
                            mapOf(
                                "score" to 2,
                                "waterActivity" to
                                        mapOf(
                                            "athleteId" to FIRST_ATHLETE_ID,
                                            "rpe" to -5, // Invalid RPE
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
                            )
                        )
            )

        client.post().uri("/athletes/$FIRST_ATHLETE_ID/competition")
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
    fun `create competition - invalid score`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "date" to DATE,
                "location" to randomString(),
                "place" to 1,
                "heats" to
                        listOf(
                            mapOf(
                                "score" to -2, // Invalid score
                                "waterActivity" to
                                        mapOf(
                                            "athleteId" to FIRST_ATHLETE_ID,
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
                            )
                        )
            )

        client.post().uri("/athletes/$FIRST_ATHLETE_ID/competition")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidScore.type.toString())
    }

    @Test
    fun `create competition - invalid trimp`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "date" to DATE,
                "location" to randomString(),
                "place" to 1,
                "heats" to
                        listOf(
                            mapOf(
                                "score" to 2,
                                "waterActivity" to
                                        mapOf(
                                            "athleteId" to FIRST_ATHLETE_ID,
                                            "rpe" to 5,
                                            "condition" to "good",
                                            "trimp" to -120, // Invalid TRIMP
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
                            )
                        )
            )

        client.post().uri("/athletes/$FIRST_ATHLETE_ID/competition")
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
    fun `create competition - invalid water maneuver id`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "date" to DATE,
                "location" to randomString(),
                "place" to 1,
                "heats" to
                        listOf(
                            mapOf(
                                "score" to 2,
                                "waterActivity" to
                                        mapOf(
                                            "athleteId" to FIRST_ATHLETE_ID,
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
                                                                            "waterManeuverId" to -1, // Invalid water maneuver ID
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
                            )
                        )
            )

        client.post().uri("/athletes/$FIRST_ATHLETE_ID/competition")
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
     * Get Competition Tests
     */

    @Test
    fun `get competition - success`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get().uri("/athletes/$FIRST_ATHLETE_ID/competition/$FIRST_COMPETITION_ID")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("date").isEqualTo(FIRST_COMPETITION_DATE)
            .jsonPath("location").isEqualTo(COMPETITION_LOCATION)
            .jsonPath("place").isEqualTo(1)
            .jsonPath("heats.length()").value<Int> { assertTrue(it > 0) }
            .jsonPath("heats[0].score").isEqualTo(85)
    }

    @Test
    fun `get competition - unauthorized`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get().uri("/athletes/$FIRST_ATHLETE_ID/competition/$FIRST_COMPETITION_ID")
            .exchange()
            .expectStatus().isUnauthorized
    }

    @Test
    fun `get competition - invalid athlete id`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val id = "invalid"

        client.get().uri("/athletes/$id/competition/$FIRST_COMPETITION_ID")
            .header("Authorization", "Bearer $FIRST_ATHLETE_TOKEN")
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidAthleteId.type.toString())
    }

    @Test
    fun `get competition - invalid competition id`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val id = "invalid"

        client.get().uri("/athletes/$FIRST_ATHLETE_ID/competition/$id")
            .header("Authorization", "Bearer $FIRST_ATHLETE_TOKEN")
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidCompetitionId.type.toString())
    }

    @Test
    fun `get competition - athlete not found`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val id = 0

        client.get().uri("/athletes/$id/competition/$FIRST_COMPETITION_ID")
            .header("Authorization", "Bearer $FIRST_ATHLETE_TOKEN")
            .exchange()
            .expectStatus().isNotFound
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.athleteNotFound.type.toString())
    }

    @Test
    fun `get competition - competition not found`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val id = 0

        client.get().uri("/athletes/$FIRST_ATHLETE_ID/competition/$id")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .exchange()
            .expectStatus().isNotFound
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.competitionNotFound.type.toString())
    }

    @Test
    fun `get competition - not athlete's coach`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get().uri("/athletes/$SECOND_ATHLETE_ID/competition/$FIRST_COMPETITION_ID")
            .header("Authorization", "Bearer $FIRST_ATHLETE_TOKEN")
            .exchange()
            .expectStatus().isForbidden
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.notAthletesCoach.type.toString())
    }

    @Test
    fun `get competition - not athlete's competition`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get().uri("/athletes/$SECOND_ATHLETE_ID/competition/$FIRST_COMPETITION_ID")
            .header("Authorization", "Bearer $SECOND_COACH_TOKEN")
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.notAthletesCompetition.type.toString())
    }

    /**
     * Update Competition Tests
     */

    @Test
    fun `update competition - success`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "date" to DATE,
                "location" to randomString(),
                "place" to 2,
                "heats" to
                        listOf(
                            mapOf(
                                "score" to 90,
                                "waterActivity" to
                                        mapOf(
                                            "athleteId" to FIRST_ATHLETE_ID,
                                            "rpe" to 6,
                                            "condition" to "excellent",
                                            "trimp" to 150,
                                            "duration" to 75,
                                            "waves" to
                                                    listOf(
                                                        mapOf(
                                                            "points" to null,
                                                            "rightSide" to true,
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
                                                    ),
                                        )
                            ),
                            mapOf(
                                "id" to 1,
                                "score" to null,
                                "waterActivity" to null
                            )
                        )
            )

        client.patch().uri("/athletes/$FIRST_ATHLETE_ID/competition/$FIRST_COMPETITION_ID")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isNoContent

    }

    /**
     * Remove Competition Tests
     */

    @Test
    fun `remove competition - success`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.delete().uri("/athletes/$FIRST_ATHLETE_ID/competition/$SECOND_COMPETITION_ID")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .exchange()
            .expectStatus().isNoContent
    }

    @Test
    fun `remove competition - unauthorized`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.delete().uri("/athletes/$FIRST_ATHLETE_ID/competition/$FIRST_COMPETITION_ID")
            .exchange()
            .expectStatus().isUnauthorized
    }

    @Test
    fun `remove competition - invalid athlete id`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val id = "invalid"

        client.delete().uri("/athletes/$id/competition/$FIRST_COMPETITION_ID")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidAthleteId.type.toString())
    }

    @Test
    fun `remove competition - invalid competition id`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val id = "invalid"

        client.delete().uri("/athletes/$FIRST_ATHLETE_ID/competition/$id")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidCompetitionId.type.toString())
    }

    @Test
    fun `remove competition - athlete not found`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val id = 0

        client.delete().uri("/athletes/$id/competition/$FIRST_COMPETITION_ID")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .exchange()
            .expectStatus().isNotFound
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.athleteNotFound.type.toString())
    }

    @Test
    fun `remove competition - competition not found`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val id = 0

        client.delete().uri("/athletes/$FIRST_ATHLETE_ID/competition/$id")
            .header("Authorization", "Bearer $FIRST_COACH_TOKEN")
            .exchange()
            .expectStatus().isNotFound
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.competitionNotFound.type.toString())
    }

    @Test
    fun `remove competition - not athlete's coach`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.delete().uri("/athletes/$FIRST_ATHLETE_ID/competition/$FIRST_COMPETITION_ID")
            .header("Authorization", "Bearer $SECOND_COACH_TOKEN")
            .exchange()
            .expectStatus().isForbidden
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.notAthletesCoach.type.toString())
    }

    @Test
    fun `remove competition - not athlete's competition`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.delete().uri("/athletes/$SECOND_ATHLETE_ID/competition/$FIRST_COMPETITION_ID")
            .header("Authorization", "Bearer $SECOND_COACH_TOKEN")
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.notAthletesCompetition.type.toString())
    }

    @Test
    fun `remove competition - user is not a coach`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.delete().uri("/athletes/$FIRST_ATHLETE_ID/competition/$FIRST_COMPETITION_ID")
            .header("Authorization", "Bearer $FIRST_ATHLETE_TOKEN")
            .exchange()
            .expectStatus().isForbidden
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.userIsNotACoach.type.toString())
    }

    companion object {
        private fun randomString() = "String_${abs(Random.nextLong())}"

        private const val DATE = "03-05-2025"
        private const val VALID_DATE = "01-01-2000"
        private const val ANOTHER_VALID_DATE = "11-01-2000"
        private const val INVALID_DATE = "32-01-2000"

        private const val FIRST_ATHLETE_ID = 3
        private const val FIRST_ATHLETE_TOKEN = "0FaEBvcKLwE1YKrLYdhHd5p61EQtJThf3mEX6o28Lgo="
        private const val FIRST_ATHLETE_USERNAME = "athlete"
        private const val FIRST_ATHLETE_NAME = "John Doe"
        private const val FIRST_ATHLETE_BIRTH_DATE = 631152000
        private const val FIRST_ATHLETE_CODE = "lnAEN21Ohq4cuorzGxMSZMKhCj2mXXSFXCO6UKzSluU="

        private const val SECOND_ATHLETE_ID = 4
        private const val SECOND_ATHLETE_TOKEN = "l71Jbu85Bl5z2E67ne5mGcRAwu69Rp4IjsRHEl4VAZ0="

        private const val ATHLETE_CHARACTERISTICS_FIRST_DATE = "25-01-2000" // date long = 948758400000
        private const val ATHLETE_CHARACTERISTICS_FIRST_DATE_LONG = 948758400000
        private const val ATHLETE_CHARACTERISTICS_SECOND_DATE = "10-01-2000" // date long = 947462400000

        private const val FIRST_COACH_TOKEN = "i_aY-4lpMqAIMuhkimTbKy4xYEuyvgFPaaTpVS0lctQ="
        private const val FIRST_COACH_ID = 1

        private const val SECOND_COACH_TOKEN = "fM5JjtPOUqtnZg1lB7jnJhXBP5gI2WbIIBoO3JhYM5M="
        private const val THIRD_ATHLETE_ID = 5

        private const val ATHLETE_HEIGHT = 181
        private const val ATHLETE_WEIGHT = 74.0f
        private const val ATHLETE_BMI = 22.587833f
        private const val ATHLETE_CALORIES = 1
        private const val ATHLETE_BODY_FAT = 1.0f
        private const val ATHLETE_WAIST_SIZE = 1
        private const val ATHLETE_ARM_SIZE = 1
        private const val ATHLETE_THIGH_SIZE = 1
        private const val ATHLETE_TRICEP_FAT = 1
        private const val ATHLETE_ABDOMEN_FAT = 1
        private const val ATHLETE_THIGH_FAT = 1

        private const val FIFTH_ATHLETE_ID = 7

        private const val FIRST_COMPETITION_ID = 1
        private const val FIRST_COMPETITION_DATE = 1746057600000
        private const val SECOND_COMPETITION_ID = 2
        private const val SECOND_COMPETITION_DATE = 1746144000000
        private const val COMPETITION_LOCATION = "Ocean Beach"
    }
}

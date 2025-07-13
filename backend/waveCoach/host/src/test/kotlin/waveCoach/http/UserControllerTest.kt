package waveCoach.http

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
class UserControllerTest {
    @LocalServerPort
    var port: Int = 0

    val BASE_URL: String
        get() = "http://localhost:$port/api"

    /**
     * Login Tests
     */

    @Test
    fun `login - success`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "username" to USERNAME,
                "password" to PASSWORD,
            )

        client.post().uri("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("id").isEqualTo(8)
            .jsonPath("username").isEqualTo(USERNAME)
            .jsonPath("token").isNotEmpty
    }

    @Test
    fun `login - username is blank`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "username" to "",
                "password" to PASSWORD,
            )

        client.post().uri("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.usernameIsBlank.type.toString())
    }

    @Test
    fun `login - password is blank`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "username" to USERNAME,
                "password" to "",
            )

        client.post().uri("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.passwordIsBlank.type.toString())
    }

    @Test
    fun `login - invalid login`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "username" to randomString(),
                "password" to randomString(),
            )

        client.post().uri("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidLogin.type.toString())
    }

    /**
     * Logout Tests
     */

    @Test
    fun `logout (authorization header) - success`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "username" to USERNAME,
                "password" to PASSWORD,
            )

        client.post().uri("/login").bodyValue(body).exchange().expectBody()
            .jsonPath("$.token").value<String> { token ->
                client.post().uri("/logout")
                    .header("Authorization", "Bearer $token")
                    .exchange()
                    .expectStatus().isOk
            }
    }

    @Test
    fun `logout (cookie) - success`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "username" to USERNAME,
                "password" to PASSWORD,
            )

        client.post().uri("/login").bodyValue(body).exchange().expectBody()
            .jsonPath("$.token").value<String> { token ->
                client.post().uri("/logout")
                    .cookie("token", token)
                    .exchange()
                    .expectStatus().isOk
            }
    }

    @Test
    fun `logout - unauthorized`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val token = randomString()

        // Authorization header
        client.post().uri("/logout")
            .header("Authorization", "Bearer $token")
            .contentType(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isUnauthorized

        // Cookie
        client.post().uri("/logout")
            .cookie("token", token)
            .contentType(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isUnauthorized
    }

    /**
     * Auth Check Tests
     */

    @Test
    fun `auth check (authorization header) - success`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "username" to USERNAME,
                "password" to PASSWORD,
            )

        client.post().uri("/login").bodyValue(body).exchange().expectBody()
            .jsonPath("$.token").value<String> { token ->
                client.get().uri("/me")
                    .header("Authorization", "Bearer $token")
                    .exchange()
                    .expectStatus().isOk
                    .expectBody()
                    .jsonPath("id").isEqualTo(8)
                    .jsonPath("username").isEqualTo(USERNAME)
            }
    }

    @Test
    fun `auth check (cookie) - success`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "username" to USERNAME,
                "password" to PASSWORD,
            )

        client.post().uri("/login").bodyValue(body).exchange().expectBody()
            .jsonPath("$.token").value<String> { token ->
                client.get().uri("/me")
                    .cookie("token", token)
                    .exchange()
                    .expectStatus().isOk
                    .expectBody()
                    .jsonPath("id").isEqualTo(8)
                    .jsonPath("username").isEqualTo(USERNAME)
            }
    }

    @Test
    fun `auth check - unauthorized`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val token = randomString()

        client.get().uri("/me")
            .header("Authorization", "Bearer $token")
            .exchange()
            .expectStatus().isUnauthorized

        // Cookie
        client.get().uri("/me")
            .cookie("token", token)
            .exchange()
            .expectStatus().isUnauthorized
    }

    /**
     * Update Username Tests
     */

    @Test
    fun `update username - success`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "newUsername" to randomString(),
            )

        client.patch().uri("/me/username")
            .header("Authorization", "Bearer $TOKEN_OF_SECOND_COACH")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isNoContent
            .expectBody()
    }

    @Test
    fun `update username - unauthorized`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "newUsername" to randomString(),
            )

        val token = randomString()

        // Authorization header
        client.patch().uri("/me/username")
            .header("Authorization", "Bearer $token")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isUnauthorized

        // Cookie
        client.patch().uri("/me/username")
            .cookie("token", randomString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isUnauthorized
    }

    @Test
    fun `update username - invalid username`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val invalidUsernames =
            listOf(
                "", // empty
                "aaa", // smaller than 4 characters
                "a".repeat(64), // bigger than 63 characters
            )

        invalidUsernames.forEach { username ->
            val body = mapOf("newUsername" to username)

            client.patch().uri("/me/username")
                .header("Authorization", "Bearer $TOKEN_OF_SECOND_COACH")
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
    fun `update user - username already exists`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "newUsername" to USERNAME,
            )

        client.patch().uri("/me/username")
            .header("Authorization", "Bearer $TOKEN_OF_SECOND_COACH")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.usernameAlreadyExists.type.toString())
    }

    /**
     * Update Password Tests
     */

    @Test
    fun `update password - success`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "oldPassword" to PASSWORD,
                "newPassword" to randomString(),
            )

        client.patch().uri("/me/password")
            .header("Authorization", "Bearer $TOKEN_OF_SECOND_COACH")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isNoContent
            .expectBody()
    }

    @Test
    fun `update password - unauthorized`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "oldPassword" to PASSWORD,
                "newPassword" to randomString(),
            )

        val token = randomString()

        // Authorization header
        client.patch().uri("/me/password")
            .header("Authorization", "Bearer $token")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isUnauthorized

        // Cookie
        client.patch().uri("/me/password")
            .cookie("token", token)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isUnauthorized
    }

    @Test
    fun `update password - insecure password`() {
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
                    "oldPassword" to PASSWORD,
                    "newPassword" to password,
                )

            client.patch().uri("/me/password")
                .header("Authorization", "Bearer $TOKEN_OF_SECOND_COACH")
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
    fun `update password - passwords are equal`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "oldPassword" to PASSWORD,
                "newPassword" to PASSWORD,
            )

        client.patch().uri("/me/password")
            .header("Authorization", "Bearer $TOKEN_OF_SECOND_COACH")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.passwordsAreEqual.type.toString())
    }

    @Test
    fun `update password - invalid old password`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body =
            mapOf(
                "oldPassword" to randomString(),
                "newPassword" to randomString(),
            )

        client.patch().uri("/me/password")
            .header("Authorization", "Bearer $TOKEN_OF_SECOND_COACH")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidOldPassword.type.toString())
    }

    companion object {
        private val USERNAME = "user3"
        private val PASSWORD = "Admin123!"
        private val TOKEN_OF_SECOND_COACH = "fM5JjtPOUqtnZg1lB7jnJhXBP5gI2WbIIBoO3JhYM5M="

        private fun randomString() = "String_${abs(Random.nextLong())}"
    }
}

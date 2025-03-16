package waveCoach.http

import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult
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
     * Create User Tests
     */

    @Test
    fun `create a user - success`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body = mapOf(
            "username" to randomString(),
            "password" to randomString(),
        )

        client.post().uri("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isCreated
            .expectHeader().exists("Location")
            .expectHeader().value("location") {
                assertTrue(it.startsWith("/api/users/"))
            }
    }

    @Test
    fun `create a user - insecure password`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val insecurePasswords = listOf(
            "Abc1234", // missing special character
            "abc123!", // missing uppercase letter
            "ABC123!", // missing lowercase letter
            "Abc!@#", // missing number
            "Abc123", // smaller than 6 characters
        )

        insecurePasswords.forEach { password ->
            val body = mapOf(
                "username" to randomString(),
                "password" to password,
            )

            client.post().uri("/users")
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
    fun `create a user - username already exists`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body = mapOf(
            "username" to USERNAME_OF_ADMIN,
            "password" to randomString(),
        )

        client.post().uri("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.usernameAlreadyExists.type.toString())
    }



    /**
     * Login Tests
     */

    @Test
    fun `login - success`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body = mapOf(
            "username" to USERNAME_OF_ADMIN,
            "password" to PASSWORD_OF_ADMIN,
        )

        client.post().uri("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("id").isEqualTo(1)
            .jsonPath("username").isEqualTo(USERNAME_OF_ADMIN)
            .jsonPath("token").isNotEmpty
    }

    @Test
    fun `login - username is blank`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body = mapOf(
            "username" to "",
            "password" to PASSWORD_OF_ADMIN,
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

        val body = mapOf(
            "username" to USERNAME_OF_ADMIN,
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

        val body = mapOf(
            "username" to randomString(),
            "password" to randomString()
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

        val body = mapOf(
            "username" to USERNAME_OF_ADMIN,
            "password" to PASSWORD_OF_ADMIN,
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

        val body = mapOf(
            "username" to USERNAME_OF_ADMIN,
            "password" to PASSWORD_OF_ADMIN,
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


    companion object {
        val USERNAME_OF_ADMIN = "admin"
        val PASSWORD_OF_ADMIN = "Admin123!"

        private fun randomString() = "String_${abs(Random.nextLong())}"
    }
}
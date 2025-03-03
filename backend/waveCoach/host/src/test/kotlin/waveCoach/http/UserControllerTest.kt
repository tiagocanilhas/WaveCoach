package pt.isel.daw.imSystem.http

import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult
import pt.isel.daw.imSystem.http.model.output.Problem
import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.Test

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UsersControllerTest {
    @LocalServerPort
    var port: Int = 0

    val BASE_URL: String
        get() = "http://localhost:$port/api"

    private fun WebTestClient.ResponseSpec.getBody(): Map<String, String>? =
        returnResult<Map<String, String>>()
            .responseBody
            .blockFirst()

    /**
     *  Test create app invite
     */

    @Test
    fun `app invite`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.post().uri("/invite")
            .header("Authorization", "Bearer $tokenOfAdminOnDb")
            .exchange()
            .expectStatus().isCreated
            .expectBody()
            .jsonPath("appInvite")
    }

    @Test
    fun `app invite - unauthorized`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.post().uri("/invite")
            .exchange()
            .expectStatus().isUnauthorized
            .expectHeader()
            .exists("WWW-Authenticate")
            .expectHeader().value("WWW-Authenticate") { wwwAuthenticate ->
                assert(wwwAuthenticate.contains("bearer"))
            }
    }

    /**
     *  Test  login
     */

    @Test
    fun login() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.post().uri("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                mapOf(
                    "username" to usernameOfAdminOnDb,
                    "password" to passwordOfAdminOnDb,
                ),
            )
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("token")
    }

    @Test
    fun `login username blank`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()
        val username = ""
        val password = randomString()

        client.post().uri("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                mapOf(
                    "username" to username,
                    "password" to password,
                ),
            )
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().exists("Content-Type")
            .expectHeader().valueEquals("Content-Type", Problem.MEDIA_TYPE)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.usernameIsBlank.type.toString())
    }

    @Test
    fun `login password blank`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()
        val username = randomString()
        val password = ""

        client.post().uri("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                mapOf(
                    "username" to username,
                    "password" to password,
                ),
            )
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().exists("Content-Type")
            .expectHeader().valueEquals("Content-Type", Problem.MEDIA_TYPE)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.passwordIsBlank.type.toString())
    }

    @Test
    fun `login invalid login`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()
        val username = "invalid login"
        val password = "invalid login"

        client.post().uri("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                mapOf(
                    "username" to username,
                    "password" to password,
                ),
            )
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().exists("Content-Type")
            .expectHeader().valueEquals("Content-Type", Problem.MEDIA_TYPE)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidLogin.type.toString())
    }

    /**
     *  Test logout
     */

    @Test
    fun `login then logout`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val token = client.post().uri("/login")
            .bodyValue(
                mapOf(
                    "username" to usernameOfAdminOnDb,
                    "password" to passwordOfAdminOnDb,
                ),
            )
            .exchange()
            .getBody()
            ?.get("token")

        client.post()
            .uri("/logout")
            .header("Authorization", "Bearer $token")
            .exchange()
            .expectStatus().isNoContent
    }

    @Test
    fun `logout not authenticated`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.post().uri("/logout")
            .exchange()
            .expectStatus().isUnauthorized
            .expectHeader()
            .exists("WWW-Authenticate")
            .expectHeader().value("WWW-Authenticate") { wwwAuthenticate ->
                assert(wwwAuthenticate.contains("bearer"))
            }
    }

    /**
     *  Test create user
     */

    @Test
    fun `create user`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()
        val username = randomString()
        val password = randomString()

        val appInvite = client.post().uri("/invite")
            .header("Authorization", "Bearer $tokenOfAdminOnDb")
            .exchange().getBody()?.get("appInvite")

        client.post().uri("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                mapOf(
                    "username" to username,
                    "password" to password,
                    "appInvite" to appInvite,
                ),
            )
            .exchange()
            .expectStatus().isCreated
            .expectHeader().exists("Location")
            .expectHeader().value("location") {
                assertTrue(it.startsWith("/api/users/"))
            }
    }

    @Test
    fun `create user with insecure password`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()
        val username = randomString()
        val password = "insecure password"

        val appInvite = client.post().uri("/invite")
            .header("Authorization", "Bearer $tokenOfAdminOnDb")
            .exchange().getBody()?.get("appInvite")

        client.post().uri("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                mapOf(
                    "username" to username,
                    "password" to password,
                    "appInvite" to appInvite,
                ),
            )
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().exists("Content-Type")
            .expectHeader().valueEquals("Content-Type", Problem.MEDIA_TYPE)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.insecurePassword.type.toString())
    }

    @Test
    fun `create user user already exists`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()
        val username = usernameOfAdminOnDb
        val password = randomString()

        val appInvite = client.post().uri("/invite")
            .header("Authorization", "Bearer $tokenOfAdminOnDb")
            .exchange().getBody()?.get("appInvite")

        client.post().uri("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                mapOf(
                    "username" to username,
                    "password" to password,
                    "appInvite" to appInvite,
                ),
            )
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().exists("Content-Type")
            .expectHeader().valueEquals("Content-Type", Problem.MEDIA_TYPE)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.userAlreadyExists.type.toString())
    }

    @Test
    fun `create user app invite not found`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()
        val username = randomString()
        val password = randomString()
        val appInvite = randomString()

        client.post().uri("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                mapOf(
                    "username" to username,
                    "password" to password,
                    "appInvite" to appInvite,
                ),
            )
            .exchange()
            .expectStatus().isNotFound
            .expectHeader().exists("Content-Type")
            .expectHeader().valueEquals("Content-Type", Problem.MEDIA_TYPE)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.appInviteNotFound.type.toString())
    }

    /**
     *  Test get user by id
     */

    @Test
    fun `get user by id`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get().uri("/users/$uidOfAdminOnDb")
            .header("Authorization", "Bearer $tokenOfAdminOnDb")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("id").isEqualTo(uidOfAdminOnDb)
            .jsonPath("username").isEqualTo(usernameOfAdminOnDb)
    }

    @Test
    fun `get user by id user not found`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()
        val uid = Random.nextInt()

        client.get().uri("/users/$uid")
            .header("Authorization", "Bearer $tokenOfAdminOnDb")
            .exchange()
            .expectStatus().isNotFound
            .expectHeader().exists("Content-Type")
            .expectHeader().valueEquals("Content-Type", Problem.MEDIA_TYPE)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.userNotFound.type.toString())
    }

    @Test
    fun `get user by id invalid user id`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()
        val uid = randomString()

        client.get().uri("/users/$uid")
            .header("Authorization", "Bearer $tokenOfAdminOnDb")
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().exists("Content-Type")
            .expectHeader().valueEquals("Content-Type", Problem.MEDIA_TYPE)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidUserId.type.toString())
    }

    @Test
    fun `get user by id not authenticated`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get().uri("/users/$uidOfAdminOnDb")
            .exchange()
            .expectStatus().isUnauthorized
            .expectHeader()
            .exists("WWW-Authenticate")
            .expectHeader().value("WWW-Authenticate") { wwwAuthenticate ->
                assert(wwwAuthenticate.contains("bearer"))
            }
    }

    /**
     * Test get users
     */

    @Test
    fun `get users`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get().uri("/users")
            .header("Authorization", "Bearer $tokenOfAdminOnDb")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("users.length()").value<Int> { length -> assertTrue(length > 0) }
    }

    @Test
    fun `get users - empty`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val username = randomString()
        client.get().uri("/users?username=$username")
            .header("Authorization", "Bearer $tokenOfAdminOnDb")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("users.length()").value<Int> { length -> assertTrue(length == 0) }
    }

    @Test
    fun `get user specific name`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get().uri("/users?username=$usernameOfAdminOnDb")
            .header("Authorization", "Bearer $tokenOfAdminOnDb")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("users.length()").value<Int> { length -> assertTrue(length == 1) }
    }

    @Test
    fun `get user relative name`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()
        val username = "User"

        client.get().uri("/users?username=$username")
            .header("Authorization", "Bearer $tokenOfAdminOnDb")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("users.length()").value<Int> { length -> assertTrue(length > 0) }
    }

    @Test
    fun `get users testing limit and skip`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()
        val limit = 1
        val skip = 1

        client.get().uri("/users?limit=$limit")
            .header("Authorization", "Bearer $tokenOfAdminOnDb")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("users.length()").value<Int> { length -> assertTrue(length == limit) }

        client.get().uri("/users?skip=$skip")
            .header("Authorization", "Bearer $tokenOfAdminOnDb")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("users.length()").value<Int> { length -> assertTrue(length <= 5) }
    }

    @Test
    fun `get users not authenticated`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get().uri("/users")
            .exchange()
            .expectStatus().isUnauthorized
            .expectHeader()
            .exists("WWW-Authenticate")
            .expectHeader().value("WWW-Authenticate") { wwwAuthenticate ->
                assert(wwwAuthenticate.contains("bearer"))
            }
    }

    /**
     *  Test get user channels
     */

    @Test
    fun `get user channels`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get().uri("/users/$uidOfAdminOnDb/channels")
            .header("Authorization", "Bearer $tokenOfAdminOnDb")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("channels.length()").value<Int> { length -> assertTrue(length > 0) }
    }

    @Test
    fun `get user channels invalid user id`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()
        val uid = randomString()

        client.get().uri("/users/$uid/channels")
            .header("Authorization", "Bearer $tokenOfAdminOnDb")
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().exists("Content-Type")
            .expectHeader().valueEquals("Content-Type", Problem.MEDIA_TYPE)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidUserId.type.toString())
    }

    @Test
    fun `get user channels not authenticated`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get().uri("/users/$uidOfAdminOnDb/channels")
            .exchange()
            .expectStatus().isUnauthorized
            .expectHeader()
            .exists("WWW-Authenticate")
            .expectHeader().value("WWW-Authenticate") { wwwAuthenticate ->
                assert(wwwAuthenticate.contains("bearer"))
            }
    }

    @Test
    fun `get user channels user id does not match Auth user id`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()
        val uid = Random.nextInt()

        client.get().uri("/users/$uid/channels")
            .header("Authorization", "Bearer $tokenOfAdminOnDb")
            .exchange()
            .expectStatus().isForbidden
            .expectHeader().exists("Content-Type")
            .expectHeader().valueEquals("Content-Type", Problem.MEDIA_TYPE)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.userCannotGetChannels.type.toString())
    }

    /**
     *  Test get user channel invites
     */

    @Test
    fun `get user channel invites`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get().uri("/users/$uidOfUser4OnDb/invites")
            .header("Authorization", "Bearer $tokenOfUser4OnDb")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("invites.length()").value<Int> { length -> assertTrue(length > 0) }
    }

    @Test
    fun `get user channel invites invalid user id`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()
        val uid = randomString()

        client.get().uri("/users/$uid/invites")
            .header("Authorization", "Bearer $tokenOfAdminOnDb")
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().exists("Content-Type")
            .expectHeader().valueEquals("Content-Type", Problem.MEDIA_TYPE)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.invalidUserId.type.toString())
    }

    @Test
    fun `get user channel invites user id does not match Auth user id`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()
        val uid = Random.nextInt()

        client.get().uri("/users/$uid/invites")
            .header("Authorization", "Bearer $tokenOfAdminOnDb")
            .exchange()
            .expectStatus().isForbidden
            .expectHeader().exists("Content-Type")
            .expectHeader().valueEquals("Content-Type", Problem.MEDIA_TYPE)
            .expectBody()
            .jsonPath("type").isEqualTo(Problem.userCannotGetChannelInvites.type.toString())
    }

    @Test
    fun `get user channel invites not authenticated`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        client.get().uri("/users/$uidOfAdminOnDb/invites")
            .exchange()
            .expectStatus().isUnauthorized
            .expectHeader()
            .exists("WWW-Authenticate")
            .expectHeader().value("WWW-Authenticate") { wwwAuthenticate ->
                assert(wwwAuthenticate.contains("bearer"))
            }
    }

    companion object {
        private fun randomString() = "String_${abs(Random.nextLong())}"

        val uidOfAdminOnDb = 1
        val usernameOfAdminOnDb = "admin"
        val passwordOfAdminOnDb = "Admin123!"
        val tokenOfAdminOnDb = "i_aY-4lpMqAIMuhkimTbKy4xYEuyvgFPaaTpVS0lctQ="

        val uidOfUser4OnDb = 4
        val tokenOfUser4OnDb = "MIxE4a2du18-3Os7K2r0k8Je-KUoEVJjRAX7Hq8Nbyg="
    }
}
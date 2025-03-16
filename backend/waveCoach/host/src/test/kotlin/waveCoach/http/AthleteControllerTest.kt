package waveCoach.http

import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import waveCoach.host.WaveCoachApplication
import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.Test

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [WaveCoachApplication::class])
class AthleteControllerTest {
    @LocalServerPort
    var port: Int = 0

    val BASE_URL: String
        get() = "http://localhost:$port/api"

    @Test
    fun `create an athlete - success`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

        val body = mapOf(
            "name" to randomString(),
            "birthDate" to "2000-01-01",
        )

        client.post().uri("/athletes")
            .header("Authorization", "Bearer $tokenOfAdminOnDb")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isCreated
            .expectHeader().exists("Location")
            .expectHeader().value("location") {
                assertTrue(it.startsWith("/api/athletes/"))
            }
    }

    companion object {
        private fun randomString() = "String_${abs(Random.nextLong())}"

        private val tokenOfAdminOnDb = "i_aY-4lpMqAIMuhkimTbKy4xYEuyvgFPaaTpVS0lctQ="
    }
}
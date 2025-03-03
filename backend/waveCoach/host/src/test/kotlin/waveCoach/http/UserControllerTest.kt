package waveCoach.http

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.web.reactive.server.WebTestClient
import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.Test

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {
    @LocalServerPort
    var port: Int = 0

    val BASE_URL: String
        get() = "http://localhost:$port/api"

    @Test
    fun `a xpto test`() {
        val client = WebTestClient.bindToServer().baseUrl(BASE_URL).build()

//        client.get().uri("/users")
//            .exchange()
//            .expectStatus().isOk
//            .expectBody()
//            .jsonPath("$.length()").value<Int> { length ->
//                assert(length >= 0)
//            }
    }

    companion object {
        private fun randomString() = "String_${abs(Random.nextLong())}"
    }
}
package waveCoach.services

import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.Assertions.assertTrue
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import waveCoach.repository.jdbi.configureWithAppRequirements
import waveCoach.utils.Failure
import waveCoach.utils.Success
import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.fail

class UsersServicesTest {
    @Test
    fun `create user`() {
        val testClock = TestClock()
        val maxTokensPerUser = 5
        val userService = createUsersService(/*testClock, maxTokensPerUser = maxTokensPerUser*/)

        val username = randomString()
        val password = randomString()

        val res = userService.createUser(username, password)
        when (res) {
            is Failure<*> -> fail("Unexpected $res")
            is Success<*> -> assertTrue(res.value > 0)
        }
    }

    companion object {
        val uidOfAdminOnDb = 1
        val usernameOfAdminOnDb = "admin"
        val passwordOfAdminOnDb = "Admin123!"
        val tokenOfAdminOnDb = "i_aY-4lpMqAIMuhkimTbKy4xYEuyvgFPaaTpVS0lctQ="

        private fun createUsersService(
//            testClock: TestClock,
//            tokenTtl: Duration = 30.days,
//            tokenRollingTtl: Duration = 30.minutes,
//            maxTokensPerUser: Int = 3,
        ) = UserServices(
//            JdbiTransactionManager(jdbi),
//            UsersDomain(
//                BCryptPasswordEncoder(),
//                Sha256TokenEncoder(),
//                UsersDomainConfig(
//                    tokenSizeInBytes = 256 / 8,
//                    tokenTtl = tokenTtl,
//                    tokenRollingTtl,
//                    maxTokensPerUser = maxTokensPerUser,
//                ),
//            ),
//            testClock,
        )

        private fun randomString() = "String_${abs(Random.nextLong())}"

        private val passwordEncoder = BCryptPasswordEncoder()

        private const val LIMIT = 10
        private const val SKIP = 0

        private val jdbi = Jdbi.create(
            PGSimpleDataSource().apply {
                setURL("jdbc:postgresql://localhost:5432/db?user=dbuser&password=changeit")
            },
        ).configureWithAppRequirements()
    }
}
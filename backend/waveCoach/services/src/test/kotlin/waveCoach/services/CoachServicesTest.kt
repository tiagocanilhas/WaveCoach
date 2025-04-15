package waveCoach.services

import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.Assertions.assertTrue
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import waveCoach.domain.Sha256TokenEncoder
import waveCoach.domain.UserDomain
import waveCoach.domain.UserDomainConfig
import waveCoach.repository.jdbi.JdbiTransactionManager
import waveCoach.repository.jdbi.configureWithAppRequirements
import waveCoach.utils.Failure
import waveCoach.utils.Success
import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.fail
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

class CoachServicesTest {
    /**
     * Create coach Tests
     */

    @Test
    fun `create coach - success`() {
        val maxTokensPerUser = 5
        val coachServices = createCoachServices(maxTokensPerUser = maxTokensPerUser)

        val username = randomString()
        val password = randomString()

        val result = coachServices.createCoach(username, password)
        when (result) {
            is Failure -> fail("Unexpected $result")
            is Success -> assertTrue(result.value > 1)
        }
    }

    @Test
    fun `create coach - insecure password`() {
        val maxTokensPerUser = 5
        val coachServices = createCoachServices(maxTokensPerUser = maxTokensPerUser)

        val insecurePasswords =
            listOf(
                "Abc1234", // missing special character
                "abc123!", // missing uppercase letter
                "ABC123!", // missing lowercase letter
                "Abc!@#", // missing number
                "Abc123", // smaller than 6 characters
            )

        insecurePasswords.forEach { password ->
            val result = coachServices.createCoach(randomString(), password)
            when (result) {
                is Failure -> assertTrue(result.value is CreateCoachError.InsecurePassword)
                is Success -> fail("Unexpected $result")
            }
        }
    }

    @Test
    fun `create coach - username already exists`() {
        val maxTokensPerUser = 5
        val coachServices = createCoachServices(maxTokensPerUser = maxTokensPerUser)

        val username = USERNAME_OF_ADMIN
        val password = randomString()

        val result = coachServices.createCoach(username, password)
        when (result) {
            is Failure -> assertTrue(result.value is CreateCoachError.UsernameAlreadyExists)
            is Success -> fail("Unexpected $result")
        }
    }

    companion object {
        private val ID_OF_ADMIN = 1
        private val USERNAME_OF_ADMIN = "admin"
        private val PASSWORD_OF_ADMIN = "Admin123!"
        private val TOKEN_OF_ADMIN = "i_aY-4lpMqAIMuhkimTbKy4xYEuyvgFPaaTpVS0lctQ="

        private fun createCoachServices(
            tokenTtl: Duration = 30.days,
            tokenRollingTtl: Duration = 30.minutes,
            maxTokensPerUser: Int = 3,
        ) = CoachServices(
            JdbiTransactionManager(jdbi),
            UserDomain(
                BCryptPasswordEncoder(),
                Sha256TokenEncoder(),
                UserDomainConfig(
                    tokenSizeInBytes = 256 / 8,
                    tokenTtl = tokenTtl,
                    tokenRollingTtl,
                    maxTokensPerUser = maxTokensPerUser,
                ),
            ),
        )

        private fun randomString() = "String_${abs(Random.nextLong())}"

        private val passwordEncoder = BCryptPasswordEncoder()

        private val jdbi =
            Jdbi.create(
                PGSimpleDataSource().apply {
                    setURL("jdbc:postgresql://localhost:5432/db?user=dbuser&password=changeit")
                },
            ).configureWithAppRequirements()
    }
}

package waveCoach.services

import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.Assertions.assertTrue
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import waveCoach.domain.AthleteDomain
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

class AthleteServicesTest {

    @Test
    fun `create athlete - success`() {
        val maxTokensPerUser = 5
        val athleteServices = createAthleteServices(maxTokensPerUser = maxTokensPerUser)

        val name = randomString()

        when (val result = athleteServices.createAthlete(name, COACH_ID, VALID_BIRTHDATE)) {
            is Failure -> fail("Unexpected $result")
            is Success -> assertTrue(result.value > 1)
        }
    }

    @Test
    fun `create athlete - invalid name`() {
        val maxTokensPerUser = 5
        val athleteServices = createAthleteServices(maxTokensPerUser = maxTokensPerUser)

        val invalidNames = listOf(
            "",
            "a".repeat(65),
        )

        invalidNames.forEach { name ->
            when (val result = athleteServices.createAthlete(name, COACH_ID, VALID_BIRTHDATE)) {
                is Failure -> assertTrue(result.value is CreateAthleteError.InvalidName)
                is Success -> fail("Unexpected $result")
            }
        }
    }

    @Test
    fun `create athlete - invalid birth date`() {
        val maxTokensPerUser = 5
        val athleteServices = createAthleteServices(maxTokensPerUser = maxTokensPerUser)

        val name = randomString()
        val invalidBirthDays = listOf(
            "2000-01-32",
            "01-01-2000",
            "2200-01-01",
        )

        invalidBirthDays.forEach { birthDate ->
            when (val result = athleteServices.createAthlete(name, COACH_ID, birthDate)) {
                is Failure -> assertTrue(result.value is CreateAthleteError.InvalidBirthDate)
                is Success -> fail("Unexpected $result")
            }
        }
    }

    companion object {
        private fun randomString() = "String_${abs(Random.nextLong())}"

        private const val VALID_BIRTHDATE = "2000-01-01"
        private const val COACH_ID = 1

        private fun createAthleteServices(
            tokenTtl: Duration = 30.days,
            tokenRollingTtl: Duration = 30.minutes,
            maxTokensPerUser: Int
        ) = AthleteServices(
            JdbiTransactionManager(jdbi),
            AthleteDomain(),
            UserDomain(
                BCryptPasswordEncoder(),
                Sha256TokenEncoder(),
                UserDomainConfig(
                    tokenSizeInBytes = 256 / 8,
                    tokenTtl = tokenTtl,
                    tokenRollingTtl,
                    maxTokensPerUser = maxTokensPerUser,
                ),

                )
        )

        private val jdbi = Jdbi.create(
            PGSimpleDataSource().apply {
                setURL("jdbc:postgresql://localhost:5432/db?user=dbuser&password=changeit")
            },
        ).configureWithAppRequirements()
    }
}
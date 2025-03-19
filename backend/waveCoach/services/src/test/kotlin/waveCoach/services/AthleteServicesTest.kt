package waveCoach.services

import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.Assertions.assertTrue
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import waveCoach.domain.*
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
    /**
     * Create Athlete Tests
     */

    @Test
    fun `create athlete - success`() {
        val athleteServices = createAthleteServices(maxTokensPerUser = MAX_TOKENS_PER_USER)

        val name = randomString()

        when (val result = athleteServices.createAthlete(name, FIRST_COACH_ID, VALID_DATE)) {
            is Failure -> fail("Unexpected $result")
            is Success -> assertTrue(result.value > 1)
        }
    }

    @Test
    fun `create athlete - invalid name`() {
        val athleteServices = createAthleteServices(maxTokensPerUser = MAX_TOKENS_PER_USER)

        val invalidNames = listOf(
            "",
            "a".repeat(65),
        )

        invalidNames.forEach { name ->
            when (val result = athleteServices.createAthlete(name, FIRST_COACH_ID, VALID_DATE)) {
                is Failure -> assertTrue(result.value is CreateAthleteError.InvalidName)
                is Success -> fail("Unexpected $result")
            }
        }
    }

    @Test
    fun `create athlete - invalid birth date`() {
        val athleteServices = createAthleteServices(maxTokensPerUser = MAX_TOKENS_PER_USER)

        val name = randomString()
        val invalidBirthDays = listOf(
            "32-01-2000",
            "2000-01-01",
            "01-01-2200",
        )

        invalidBirthDays.forEach { birthDate ->
            when (val result = athleteServices.createAthlete(name, FIRST_COACH_ID, birthDate)) {
                is Failure -> assertTrue(result.value is CreateAthleteError.InvalidBirthDate)
                is Success -> fail("Unexpected $result")
            }
        }
    }


    /**
     * Remove Athlete Tests
     */

    @Test
    fun `remove athlete - success`() {
        val athleteServices = createAthleteServices(maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (val removeResult = athleteServices.removeAthlete(SECOND_COACH_ID, SECOND_ATHLETE_ID)) {
            is Failure -> fail("Unexpected $removeResult")
            is Success -> assertTrue(removeResult.value == SECOND_ATHLETE_ID)
        }
    }

    @Test
    fun `remove athlete - athlete not found`() {
        val athleteServices = createAthleteServices(maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (val removeResult = athleteServices.removeAthlete(FIRST_COACH_ID, 0)) {
            is Failure -> assertTrue(removeResult.value is RemoveAthleteError.AthleteNotFound)
            is Success -> fail("Unexpected $removeResult")
        }
    }

    @Test
    fun `remove athlete - not athlete's coach`() {
        val athleteServices = createAthleteServices(maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (val removeResult = athleteServices.removeAthlete(SECOND_COACH_ID, FIRST_ATHLETE_ID)) {
            is Failure -> assertTrue(removeResult.value is RemoveAthleteError.NotAthletesCoach)
            is Success -> fail("Unexpected $removeResult")
        }
    }


    /**
     * Create Characteristics Tests
     */

    @Test
    fun `create characteristics - success`() {
        val athleteServices = createAthleteServices(maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (val result = athleteServices.createCharacteristics(
            FIRST_COACH_ID,
            FIRST_ATHLETE_ID,
            VALID_DATE,
            1,
            1.1f,
            1,
            1,
            1,
            1,
            1.1f,
            1.1f,
        )) {
            is Failure -> fail("Unexpected $result")
            is Success -> assertTrue(result.value > 0)
        }
    }

    @Test
    fun `create characteristics - success without date`() {
        val athleteServices = createAthleteServices(maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (val result = athleteServices.createCharacteristics(
            FIRST_COACH_ID,
            FIRST_ATHLETE_ID,
            null,
            1,
            1.1f,
            1,
            1,
            1,
            1,
            1.1f,
            1.1f,
        )) {
            is Failure -> fail("Unexpected $result")
            is Success -> assertTrue(result.value > 0)
        }
    }

    @Test
    fun `create characteristics - invalid date`() {
        val athleteServices = createAthleteServices(maxTokensPerUser = MAX_TOKENS_PER_USER)

        val invalidDates = listOf(
            "32-01-2000",
            "2000-01-01",
            "01-01-2200",
        )

        invalidDates.forEach { date ->
            when (val result = athleteServices.createCharacteristics(
                FIRST_COACH_ID,
                FIRST_ATHLETE_ID,
                date,
                1,
                1.1f,
                1,
                1,
                1,
                1,
                1.1f,
                1.1f,
            )) {
                is Failure -> assertTrue(result.value is CreateCharacteristicsError.InvalidDate)
                is Success -> fail("Unexpected $result")
            }
        }
    }

    @Test
    fun `create characteristics - invalid characteristics`() {
        val athleteServices = createAthleteServices(maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (val result = athleteServices.createCharacteristics(
            FIRST_COACH_ID,
            FIRST_ATHLETE_ID,
            VALID_DATE,
            -1,
            -1f,
            -1,
            -1,
            -1,
            -1,
            -1f,
            -1f,
        )
        ) {
            is Failure -> assertTrue(result.value is CreateCharacteristicsError.InvalidCharacteristics)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `create characteristics - athlete not found`() {
        val athleteServices = createAthleteServices(maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (val result = athleteServices.createCharacteristics(
            FIRST_COACH_ID,
            0,
            VALID_DATE,
            1,
            1.1f,
            1,
            1,
            1,
            1,
            1.1f,
            1.1f,
        )
        ) {
            is Failure -> assertTrue(result.value is CreateCharacteristicsError.AthleteNotFound)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `create characteristics - not athlete's coach`() {
        val athleteServices = createAthleteServices(maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (val result = athleteServices.createCharacteristics(
            SECOND_COACH_ID,
            FIRST_ATHLETE_ID,
            VALID_DATE,
            1,
            1.1f,
            1,
            1,
            1,
            1,
            1.1f,
            1.1f,
        )
        ) {
            is Failure -> assertTrue(result.value is CreateCharacteristicsError.NotAthletesCoach)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `update characteristics - success`() {
        val athleteServices = createAthleteServices(maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (val result = athleteServices.updateCharacteristics(
            FIRST_COACH_ID,
            FIRST_ATHLETE_ID,
            VALID_DATE,
            1,
            1.1f,
            1,
            1,
            1,
            1,
            1.1f,
            1.1f,
        )) {
            is Failure -> fail("Unexpected $result")
            is Success -> assertTrue(result.value == FIRST_ATHLETE_ID)
        }
    }

    @Test
    fun `update characteristics - invalid date`() {
        val athleteServices = createAthleteServices(maxTokensPerUser = MAX_TOKENS_PER_USER)

        val invalidDates = listOf(
            "32-01-2000",
            "2000-01-01",
            "01-01-2200",
        )

        invalidDates.forEach { date ->
            when (val result = athleteServices.updateCharacteristics(
                FIRST_COACH_ID,
                FIRST_ATHLETE_ID,
                date,
                1,
                1.1f,
                1,
                1,
                1,
                1,
                1.1f,
                1.1f,
            )) {
                is Failure -> assertTrue(result.value is UpdateCharacteristicsError.InvalidDate)
                is Success -> fail("Unexpected $result")
            }
        }
    }

    @Test
    fun `update characteristics - invalid characteristics`() {
        val athleteServices = createAthleteServices(maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (val result = athleteServices.updateCharacteristics(
            FIRST_COACH_ID,
            FIRST_ATHLETE_ID,
            VALID_DATE,
            -1,
            -1f,
            -1,
            -1,
            -1,
            -1,
            -1f,
            -1f,
        )) {
            is Failure -> assertTrue(result.value is UpdateCharacteristicsError.InvalidCharacteristics)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `update characteristics - athlete not found`() {
        val athleteServices = createAthleteServices(maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (val result = athleteServices.updateCharacteristics(
            FIRST_COACH_ID,
            0,
            VALID_DATE,
            1,
            1.1f,
            1,
            1,
            1,
            1,
            1.1f,
            1.1f,
        )) {
            is Failure -> assertTrue(result.value is UpdateCharacteristicsError.AthleteNotFound)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `update characteristics - not athlete's coach`() {
        val athleteServices = createAthleteServices(maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (val result = athleteServices.updateCharacteristics(
            SECOND_COACH_ID,
            FIRST_ATHLETE_ID,
            VALID_DATE,
            1,
            1.1f,
            1,
            1,
            1,
            1,
            1.1f,
            1.1f,
        )) {
            is Failure -> assertTrue(result.value is UpdateCharacteristicsError.NotAthletesCoach)
            is Success -> fail("Unexpected $result")
        }
    }

    companion object {
        private fun randomString() = "String_${abs(Random.nextLong())}"

        private const val VALID_DATE = "01-01-2000"
        private const val FIRST_COACH_ID = 1
        private const val SECOND_COACH_ID = 2
        private const val FIRST_ATHLETE_ID = 3
        private const val SECOND_ATHLETE_ID = 4

        private const val MAX_TOKENS_PER_USER = 5

        private fun createAthleteServices(
            tokenTtl: Duration = 30.days,
            tokenRollingTtl: Duration = 30.minutes,
            maxTokensPerUser: Int
        ) = AthleteServices(
            JdbiTransactionManager(jdbi),
            AthleteDomain(),
            CharacteristicsDomain(),
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
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
     * Get Athlete Tests
     */

    @Test
    fun `get athlete - success`() {
        val athleteServices = createAthleteServices(maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (val result = athleteServices.getAthlete(FIRST_COACH_ID, FIRST_ATHLETE_ID)) {
            is Failure -> fail("Unexpected $result")
            is Success -> assertTrue(
                result.value.uid == FIRST_ATHLETE_ID
                        && result.value.coach == FIRST_COACH_ID
                        && result.value.name == FIRST_ATHLETE_NAME
                        && result.value.birthDate == FIRST_ATHLETE_BIRTH_DATE
            )
        }
    }

    @Test
    fun `get athlete - athlete not found`() {
        val athleteServices = createAthleteServices(maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (val result = athleteServices.getAthlete(FIRST_COACH_ID, 0)) {
            is Failure -> assertTrue(result.value is GetAthleteError.AthleteNotFound)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `get athlete - not athlete's coach`() {
        val athleteServices = createAthleteServices(maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (val result = athleteServices.getAthlete(SECOND_COACH_ID, FIRST_ATHLETE_ID)) {
            is Failure -> assertTrue(result.value is GetAthleteError.NotAthletesCoach)
            is Success -> fail("Unexpected $result")
        }
    }

    /**
     * Get Athlete List Tests
     */

    @Test
    fun `get athlete list - success`() {
        val athleteServices = createAthleteServices(maxTokensPerUser = MAX_TOKENS_PER_USER)

        val result = athleteServices.getAthletes(FIRST_COACH_ID)
        assertTrue(result.isNotEmpty())
    }

    /**
     * Update Athlete Tests
     */

    @Test
    fun `update athlete - success`() {
        val athleteServices = createAthleteServices(maxTokensPerUser = MAX_TOKENS_PER_USER)

        val name = randomString()

        when (val result = athleteServices.updateAthlete(SECOND_COACH_ID, THIRD_ATHLETE_ID, name, VALID_DATE)) {
            is Failure -> fail("Unexpected $result")
            is Success -> assertTrue(result.value == THIRD_ATHLETE_ID)
        }
    }

    @Test
    fun `update athlete - invalid name`() {
        val athleteServices = createAthleteServices(maxTokensPerUser = MAX_TOKENS_PER_USER)

        val invalidNames = listOf(
            "",
            "a".repeat(65),
        )

        invalidNames.forEach { name ->
            when (val result = athleteServices.updateAthlete(FIRST_COACH_ID, FIRST_ATHLETE_ID, name, VALID_DATE)) {
                is Failure -> assertTrue(result.value is UpdateAthleteError.InvalidName)
                is Success -> fail("Unexpected $result")
            }
        }
    }

    @Test
    fun `update athlete - invalid birth date`() {
        val athleteServices = createAthleteServices(maxTokensPerUser = MAX_TOKENS_PER_USER)

        val name = randomString()
        val invalidBirthDays = listOf(
            "32-01-2000",
            "2000-01-01",
            "01-01-2200",
        )

        invalidBirthDays.forEach { birthDate ->
            when (val result = athleteServices.updateAthlete(FIRST_COACH_ID, FIRST_ATHLETE_ID, name, birthDate)) {
                is Failure -> assertTrue(result.value is UpdateAthleteError.InvalidBirthDate)
                is Success -> fail("Unexpected $result")
            }
        }
    }

    @Test
    fun `update athlete - athlete not found`() {
        val athleteServices = createAthleteServices(maxTokensPerUser = MAX_TOKENS_PER_USER)

        val name = randomString()
        val aid = 0

        when (val result = athleteServices.updateAthlete(FIRST_COACH_ID, aid, name, VALID_DATE)) {
            is Failure -> assertTrue(result.value is UpdateAthleteError.AthleteNotFound)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `update athlete - not athlete's coach`() {
        val athleteServices = createAthleteServices(maxTokensPerUser = MAX_TOKENS_PER_USER)

        val name = randomString()

        when (val result = athleteServices.updateAthlete(SECOND_COACH_ID, FIRST_ATHLETE_ID, name, VALID_DATE)) {
            is Failure -> assertTrue(result.value is UpdateAthleteError.NotAthletesCoach)
            is Success -> fail("Unexpected $result")
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
            1.0f,
            1,
            1,
            1,
            1,
            1,
            1
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
            1.0f,
            1,
            1,
            1,
            1,
            1,
            1
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
                1.0f,
                1,
                1,
                1,
                1,
                1,
                1
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
            -1.0f,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1
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
            1.0f,
            1,
            1,
            1,
            1,
            1,
            1
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
            1.0f,
            1,
            1,
            1,
            1,
            1,
            1
        )
        ) {
            is Failure -> assertTrue(result.value is CreateCharacteristicsError.NotAthletesCoach)
            is Success -> fail("Unexpected $result")
        }
    }

    /**
     * Get Characteristics Tests
     */

    @Test
    fun `get characteristics - success`() {
        val athleteServices = createAthleteServices(maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (val result =
            athleteServices.getCharacteristics(FIRST_COACH_ID, FIRST_ATHLETE_ID, ATHLETE_CHARACTERISTICS_FIRST_DATE)) {
            is Failure -> fail("Unexpected $result")
            is Success -> assertTrue(
                result.value.uid == FIRST_ATHLETE_ID
                        && result.value.date == ATHLETE_CHARACTERISTICS_FIRST_DATE_LONG
                        && result.value.height == ATHLETE_HEIGHT
                        && result.value.weight == ATHLETE_WEIGHT
                        && result.value.bmi == ATHLETE_BMI
                        && result.value.calories == ATHLETE_CALORIES
                        && result.value.bodyFat == ATHLETE_BODY_FAT
                        && result.value.waistSize == ATHLETE_WAIST_SIZE
                        && result.value.armSize == ATHLETE_ARM_SIZE
                        && result.value.thighSize == ATHLETE_THIGH_SIZE
                        && result.value.tricepFat == ATHLETE_TRICEP_FAT
                        && result.value.abdomenFat == ATHLETE_ABDOMEN_FAT
                        && result.value.thighFat == ATHLETE_THIGH_FAT
            )
        }
    }

    @Test
    fun `get characteristics - invalid date`() {
        val athleteServices = createAthleteServices(maxTokensPerUser = MAX_TOKENS_PER_USER)

        val invalidDates = listOf(
            "32-01-2000",
            "2000-01-01",
            "01-01-2200",
        )

        invalidDates.forEach { date ->
            when (val result = athleteServices.getCharacteristics(FIRST_COACH_ID, FIRST_ATHLETE_ID, date)) {
                is Failure -> assertTrue(result.value is GetCharacteristicsError.InvalidDate)
                is Success -> fail("Unexpected $result")
            }
        }
    }

    @Test
    fun `get characteristics - athlete not found`() {
        val athleteServices = createAthleteServices(maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (val result = athleteServices.getCharacteristics(FIRST_COACH_ID, 0, ATHLETE_CHARACTERISTICS_FIRST_DATE)) {
            is Failure -> assertTrue(result.value is GetCharacteristicsError.AthleteNotFound)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `get characteristics - characteristics not found`() {
        val athleteServices = createAthleteServices(maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (val result = athleteServices.getCharacteristics(FIRST_COACH_ID, FIRST_ATHLETE_ID, VALID_DATE)) {
            is Failure -> assertTrue(result.value is GetCharacteristicsError.CharacteristicsNotFound)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `get characteristics - not athlete's coach`() {
        val athleteServices = createAthleteServices(maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (val result =
            athleteServices.getCharacteristics(SECOND_COACH_ID, FIRST_ATHLETE_ID, ATHLETE_CHARACTERISTICS_FIRST_DATE)) {
            is Failure -> assertTrue(result.value is GetCharacteristicsError.NotAthletesCoach)
            is Success -> fail("Unexpected $result")
        }
    }

    /**
     * Get Characteristics List Tests
     */

    @Test
    fun `get characteristics list - success`() {
        val athleteServices = createAthleteServices(maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (val result = athleteServices.getCharacteristicsList(FIRST_COACH_ID, FIRST_ATHLETE_ID)) {
            is Failure -> fail("Unexpected $result")
            is Success -> assertTrue(result.value.isNotEmpty())
        }
    }

    @Test
    fun `get characteristics list - athlete not found`() {
        val athleteServices = createAthleteServices(maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (val result = athleteServices.getCharacteristicsList(FIRST_COACH_ID, 0)) {
            is Failure -> assertTrue(result.value is GetCharacteristicsListError.AthleteNotFound)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `get characteristics list - not athlete's coach`() {
        val athleteServices = createAthleteServices(maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (val result = athleteServices.getCharacteristicsList(SECOND_COACH_ID, FIRST_ATHLETE_ID)) {
            is Failure -> assertTrue(result.value is GetCharacteristicsListError.NotAthletesCoach)
            is Success -> fail("Unexpected $result")
        }
    }

    /**
     * Update Characteristics Tests
     */

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
            1.1f,
            1,
            1,
            1,
            1,
            1,
            1
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
                1.1f,
                1,
                1,
                1,
                1,
                1,
                1
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
            -1.0f,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1
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
            1.1f,
            1,
            1,
            1,
            1,
            1,
            1
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
            1.1f,
            1,
            1,
            1,
            1,
            1,
            1
        )) {
            is Failure -> assertTrue(result.value is UpdateCharacteristicsError.NotAthletesCoach)
            is Success -> fail("Unexpected $result")
        }
    }

    /**
     * Remove Characteristics Tests
     */

    @Test
    fun `remove characteristics - success`() {
        val athleteServices = createAthleteServices(maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (val result = athleteServices.removeCharacteristics(
            FIRST_COACH_ID,
            FIRST_ATHLETE_ID,
            ATHLETE_CHARACTERISTICS_SECOND_DATE
        )) {
            is Failure -> fail("Unexpected $result")
            is Success -> assertTrue(result.value == FIRST_ATHLETE_ID)
        }
    }

    @Test
    fun `remove characteristics - invalid date`() {
        val athleteServices = createAthleteServices(maxTokensPerUser = MAX_TOKENS_PER_USER)

        val invalidDates = listOf(
            "32-01-2000",
            "2000-01-01",
            "01-01-2200",
        )

        invalidDates.forEach { date ->
            when (val result = athleteServices.removeCharacteristics(
                FIRST_COACH_ID,
                FIRST_ATHLETE_ID,
                date
            )) {
                is Failure -> assertTrue(result.value is RemoveCharacteristicsError.InvalidDate)
                is Success -> fail("Unexpected $result")
            }
        }
    }

    @Test
    fun `remove characteristics - athlete not found`() {
        val athleteServices = createAthleteServices(maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (val result = athleteServices.removeCharacteristics(
            FIRST_COACH_ID,
            0,
            ATHLETE_CHARACTERISTICS_FIRST_DATE
        )) {
            is Failure -> assertTrue(result.value is RemoveCharacteristicsError.AthleteNotFound)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `remove characteristics - characteristics not found`() {
        val athleteServices = createAthleteServices(maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (val result = athleteServices.removeCharacteristics(
            FIRST_COACH_ID,
            FIRST_ATHLETE_ID,
            VALID_DATE
        )) {
            is Failure -> assertTrue(result.value is RemoveCharacteristicsError.CharacteristicsNotFound)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `remove characteristics - not athlete's coach`() {
        val athleteServices = createAthleteServices(maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (val result = athleteServices.removeCharacteristics(
            SECOND_COACH_ID,
            FIRST_ATHLETE_ID,
            ATHLETE_CHARACTERISTICS_FIRST_DATE
        )) {
            is Failure -> assertTrue(result.value is RemoveCharacteristicsError.NotAthletesCoach)
            is Success -> fail("Unexpected $result")
        }
    }

    companion object {
        private fun randomString() = "String_${abs(Random.nextLong())}"

        private const val VALID_DATE = "01-01-2000"
        private const val FIRST_COACH_ID = 1
        private const val SECOND_COACH_ID = 2
        private const val FIRST_ATHLETE_ID = 3
        private const val FIRST_ATHLETE_NAME = "John Doe"
        private const val FIRST_ATHLETE_BIRTH_DATE: Long = 631152000
        private const val SECOND_ATHLETE_ID = 4
        private const val THIRD_ATHLETE_ID = 5
        private const val ATHLETE_CHARACTERISTICS_FIRST_DATE = "25-01-2000" // date long = 948758400000
        private const val ATHLETE_CHARACTERISTICS_FIRST_DATE_LONG = 948758400000
        private const val ATHLETE_CHARACTERISTICS_SECOND_DATE = "10-01-2000" // date long = 947462400000

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
            ),
        )

        private val jdbi = Jdbi.create(
            PGSimpleDataSource().apply {
                setURL("jdbc:postgresql://localhost:5432/db?user=dbuser&password=changeit")
            },
        ).configureWithAppRequirements()
    }
}
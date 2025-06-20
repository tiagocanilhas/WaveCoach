package waveCoach.services

import com.cloudinary.Cloudinary
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
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        val name = randomString()

        when (val result = athleteServices.createAthlete(name, FIRST_COACH_ID, VALID_DATE, null)) {
            is Failure -> fail("Unexpected $result")
            is Success -> assertTrue(result.value > 1)
        }
    }

    @Test
    fun `create athlete - invalid name`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        val invalidNames =
            listOf(
                "",
                "a".repeat(65),
            )

        invalidNames.forEach { name ->
            when (val result = athleteServices.createAthlete(name, FIRST_COACH_ID, VALID_DATE, null)) {
                is Failure -> assertTrue(result.value is CreateAthleteError.InvalidName)
                is Success -> fail("Unexpected $result")
            }
        }
    }

    @Test
    fun `create athlete - invalid birth date`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        val name = randomString()
        val invalidBirthDays =
            listOf(
                "32-01-2000",
                "2000-01-01",
                "01-01-2200",
            )

        invalidBirthDays.forEach { birthdate ->
            when (val result = athleteServices.createAthlete(name, FIRST_COACH_ID, birthdate, null)) {
                is Failure -> assertTrue(result.value is CreateAthleteError.InvalidBirthdate)
                is Success -> fail("Unexpected $result")
            }
        }
    }

    /**
     * Get Athlete Tests
     */

    @Test
    fun `get athlete - success`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (val result = athleteServices.getAthlete(FIRST_COACH_ID, FIRST_ATHLETE_ID)) {
            is Failure -> fail("Unexpected $result")
            is Success ->
                assertTrue(
                    result.value.uid == FIRST_ATHLETE_ID &&
                            result.value.coach == FIRST_COACH_ID &&
                            result.value.name == FIRST_ATHLETE_NAME &&
                            result.value.birthdate == FIRST_ATHLETE_BIRTH_DATE,
                )
        }
    }

    @Test
    fun `get athlete - athlete not found`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (val result = athleteServices.getAthlete(FIRST_COACH_ID, 0)) {
            is Failure -> assertTrue(result.value is GetAthleteError.AthleteNotFound)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `get athlete - not athlete's coach`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

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
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        val result = athleteServices.getAthletes(FIRST_COACH_ID)
        assertTrue(result.isNotEmpty())
    }

    /**
     * Update Athlete Tests
     */

    @Test
    fun `update athlete - success`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        val name = randomString()

        when (val result = athleteServices.updateAthlete(SECOND_COACH_ID, THIRD_ATHLETE_ID, name, VALID_DATE)) {
            is Failure -> fail("Unexpected $result")
            is Success -> assertTrue(result.value == THIRD_ATHLETE_ID)
        }
    }

    @Test
    fun `update athlete - invalid name`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        val invalidNames =
            listOf(
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
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        val name = randomString()
        val invalidBirthDays =
            listOf(
                "32-01-2000",
                "2000-01-01",
                "01-01-2200",
            )

        invalidBirthDays.forEach { birthdate ->
            when (val result = athleteServices.updateAthlete(FIRST_COACH_ID, FIRST_ATHLETE_ID, name, birthdate)) {
                is Failure -> assertTrue(result.value is UpdateAthleteError.Invalidbirthdate)
                is Success -> fail("Unexpected $result")
            }
        }
    }

    @Test
    fun `update athlete - athlete not found`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        val name = randomString()
        val aid = 0

        when (val result = athleteServices.updateAthlete(FIRST_COACH_ID, aid, name, VALID_DATE)) {
            is Failure -> assertTrue(result.value is UpdateAthleteError.AthleteNotFound)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `update athlete - not athlete's coach`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

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
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (val removeResult = athleteServices.removeAthlete(SECOND_COACH_ID, SECOND_ATHLETE_ID)) {
            is Failure -> fail("Unexpected $removeResult")
            is Success -> assertTrue(removeResult.value == SECOND_ATHLETE_ID)
        }
    }

    @Test
    fun `remove athlete - athlete not found`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (val removeResult = athleteServices.removeAthlete(FIRST_COACH_ID, 0)) {
            is Failure -> assertTrue(removeResult.value is RemoveAthleteError.AthleteNotFound)
            is Success -> fail("Unexpected $removeResult")
        }
    }

    @Test
    fun `remove athlete - not athlete's coach`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (val removeResult = athleteServices.removeAthlete(SECOND_COACH_ID, FIRST_ATHLETE_ID)) {
            is Failure -> assertTrue(removeResult.value is RemoveAthleteError.NotAthletesCoach)
            is Success -> fail("Unexpected $removeResult")
        }
    }

    /**
     *  Generate Code Tests
     */

    @Test
    fun `generate code - success`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (val result = athleteServices.generateCode(SECOND_COACH_ID, FOURTH_ATHLETE_ID)) {
            is Failure -> fail("Unexpected $result")
            is Success -> {
                assertTrue(result.value.code.isNotEmpty())
                assertTrue(result.value.expirationDate.epochSeconds > 0)
            }
        }
    }

    @Test
    fun `generate code - athlete not found`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (val result = athleteServices.generateCode(FIRST_COACH_ID, 0)) {
            is Failure -> assertTrue(result.value is GenerateCodeError.AthleteNotFound)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `generate code - not athlete's coach`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (val result = athleteServices.generateCode(SECOND_COACH_ID, FIRST_ATHLETE_ID)) {
            is Failure -> assertTrue(result.value is GenerateCodeError.NotAthletesCoach)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `generate code - credentials already changed`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (val result = athleteServices.generateCode(FIRST_COACH_ID, FIRST_ATHLETE_ID)) {
            is Failure -> assertTrue(result.value is GenerateCodeError.CredentialsAlreadyChanged)
            is Success -> fail("Unexpected $result")
        }
    }

    /**
     * Get username by code tests
     */

    @Test
    fun `get username by code - success`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        val code = (athleteServices.generateCode(FIRST_COACH_ID, FIRST_ATHLETE_ID) as Success).value.code

        when (val result = athleteServices.getUsernameByCode(code)) {
            is Failure -> fail("Unexpected $result")
            is Success -> assertTrue(result.value == FIRST_ATHLETE_USERNAME)
        }
    }

    @Test
    fun `get username by code - invalid code`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        val code = "invalid_code"

        when (val result = athleteServices.getUsernameByCode(code)) {
            is Failure -> assertTrue(result.value is GetUsernameByCodeError.InvalidCode)
            is Success -> fail("Unexpected $result")
        }
    }

    /**
     * Change Credentials Tests
     */

    @Test
    fun `change credentials - success`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        val code = (athleteServices.generateCode(FIRST_COACH_ID, FIRST_ATHLETE_ID) as Success).value.code

        when (val result = athleteServices.changeCredentials(code, randomString(), randomString())) {
            is Failure -> fail("Unexpected $result")
            is Success -> assertTrue(result.value == FIRST_ATHLETE_ID)
        }
    }

    @Test
    fun `change credentials - invalid username`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        val invalidUsernames =
            listOf(
                "",
                "a".repeat(65),
            )

        val code = (athleteServices.generateCode(FIRST_COACH_ID, FIRST_ATHLETE_ID) as Success).value.code

        invalidUsernames.forEach { username ->
            when (val result = athleteServices.changeCredentials(code, username, randomString())) {
                is Failure -> assertTrue(result.value is ChangeCredentialsError.InvalidUsername)
                is Success -> fail("Unexpected $result")
            }
        }
    }

    @Test
    fun `change credentials - insecure password`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        val insecurePasswords =
            listOf(
                "", // empty
                "Abc1234", // missing special character
                "abc123!", // missing uppercase letter
                "ABC123!", // missing lowercase letter
                "Abc!@#", // missing number
                "Abc12!", // smaller than 6 characters
            )

        val code = (athleteServices.generateCode(SECOND_COACH_ID, FOURTH_ATHLETE_ID) as Success).value.code

        insecurePasswords.forEach { password ->
            when (val result = athleteServices.changeCredentials(code, randomString(), password)) {
                is Failure -> assertTrue(result.value is ChangeCredentialsError.InsecurePassword)
                is Success -> fail("Unexpected $result")
            }
        }
    }

    @Test
    fun `change credentials - invalid code`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        val code = "invalid_code"

        when (val result = athleteServices.changeCredentials(code, randomString(), randomString())) {
            is Failure -> assertTrue(result.value is ChangeCredentialsError.InvalidCode)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `change credentials - username already exists`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        val code = (athleteServices.generateCode(SECOND_COACH_ID, FOURTH_ATHLETE_ID) as Success).value.code

        when (val result = athleteServices.changeCredentials(code, THIRD_ATHLETE_USERNAME, randomString())) {
            is Failure -> assertTrue(result.value is ChangeCredentialsError.UsernameAlreadyExists)
            is Success -> fail("Unexpected $result")
        }
    }

    /**
     * Create Characteristics Tests
     */

    @Test
    fun `create characteristics - success`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (
            val result =
                athleteServices.createCharacteristics(
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
                    1,
                )
        ) {
            is Failure -> fail("Unexpected $result")
            is Success -> assertTrue(result.value > 0)
        }
    }

    @Test
    fun `create characteristics - success without date`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (
            val result =
                athleteServices.createCharacteristics(
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
                    1,
                )
        ) {
            is Failure -> fail("Unexpected $result")
            is Success -> assertTrue(result.value > 0)
        }
    }

    @Test
    fun `create characteristics - invalid date`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        val invalidDates =
            listOf(
                "32-01-2000",
                "2000-01-01",
                "01-01-2200",
            )

        invalidDates.forEach { date ->
            when (
                val result =
                    athleteServices.createCharacteristics(
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
                        1,
                    )
            ) {
                is Failure -> assertTrue(result.value is CreateCharacteristicsError.InvalidDate)
                is Success -> fail("Unexpected $result")
            }
        }
    }

    @Test
    fun `create characteristics - invalid characteristics`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (
            val result =
                athleteServices.createCharacteristics(
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
                    -1,
                )
        ) {
            is Failure -> assertTrue(result.value is CreateCharacteristicsError.InvalidCharacteristics)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `create characteristics - athlete not found`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (
            val result =
                athleteServices.createCharacteristics(
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
                    1,
                )
        ) {
            is Failure -> assertTrue(result.value is CreateCharacteristicsError.AthleteNotFound)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `create characteristics - not athlete's coach`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (
            val result =
                athleteServices.createCharacteristics(
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
                    1,
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
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (
            val result =
                athleteServices.getCharacteristics(FIRST_COACH_ID, FIRST_ATHLETE_ID, ATHLETE_CHARACTERISTICS_FIRST_DATE)
        ) {
            is Failure -> fail("Unexpected $result")
            is Success ->
                assertTrue(
                    result.value.uid == FIRST_ATHLETE_ID &&
                            result.value.date == ATHLETE_CHARACTERISTICS_FIRST_DATE_LONG &&
                            result.value.height == ATHLETE_HEIGHT &&
                            result.value.weight == ATHLETE_WEIGHT &&
                            result.value.bmi == ATHLETE_BMI &&
                            result.value.calories == ATHLETE_CALORIES &&
                            result.value.bodyFat == ATHLETE_BODY_FAT &&
                            result.value.waistSize == ATHLETE_WAIST_SIZE &&
                            result.value.armSize == ATHLETE_ARM_SIZE &&
                            result.value.thighSize == ATHLETE_THIGH_SIZE &&
                            result.value.tricepFat == ATHLETE_TRICEP_FAT &&
                            result.value.abdomenFat == ATHLETE_ABDOMEN_FAT &&
                            result.value.thighFat == ATHLETE_THIGH_FAT,
                )
        }
    }

    @Test
    fun `get characteristics - invalid date`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        val invalidDates =
            listOf(
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
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (val result = athleteServices.getCharacteristics(FIRST_COACH_ID, 0, ATHLETE_CHARACTERISTICS_FIRST_DATE)) {
            is Failure -> assertTrue(result.value is GetCharacteristicsError.AthleteNotFound)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `get characteristics - characteristics not found`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (val result = athleteServices.getCharacteristics(FIRST_COACH_ID, FIRST_ATHLETE_ID, VALID_DATE)) {
            is Failure -> assertTrue(result.value is GetCharacteristicsError.CharacteristicsNotFound)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `get characteristics - not athlete's coach`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (
            val result =
                athleteServices.getCharacteristics(
                    SECOND_COACH_ID,
                    FIRST_ATHLETE_ID,
                    ATHLETE_CHARACTERISTICS_FIRST_DATE,
                )
        ) {
            is Failure -> assertTrue(result.value is GetCharacteristicsError.NotAthletesCoach)
            is Success -> fail("Unexpected $result")
        }
    }

    /**
     * Get Characteristics List Tests
     */

    @Test
    fun `get characteristics list - success`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (val result = athleteServices.getCharacteristicsList(FIRST_COACH_ID, FIRST_ATHLETE_ID)) {
            is Failure -> fail("Unexpected $result")
            is Success -> assertTrue(result.value.isNotEmpty())
        }
    }

    @Test
    fun `get characteristics list - athlete not found`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (val result = athleteServices.getCharacteristicsList(FIRST_COACH_ID, 0)) {
            is Failure -> assertTrue(result.value is GetCharacteristicsListError.AthleteNotFound)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `get characteristics list - not athlete's coach`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

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
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (
            val result =
                athleteServices.updateCharacteristics(
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
                    1,
                )
        ) {
            is Failure -> fail("Unexpected $result")
            is Success -> assertTrue(result.value == FIRST_ATHLETE_ID)
        }
    }

    @Test
    fun `update characteristics - invalid date`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        val invalidDates =
            listOf(
                "32-01-2000",
                "2000-01-01",
                "01-01-2200",
            )

        invalidDates.forEach { date ->
            when (
                val result =
                    athleteServices.updateCharacteristics(
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
                        1,
                    )
            ) {
                is Failure -> assertTrue(result.value is UpdateCharacteristicsError.InvalidDate)
                is Success -> fail("Unexpected $result")
            }
        }
    }

    @Test
    fun `update characteristics - invalid characteristics`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (
            val result =
                athleteServices.updateCharacteristics(
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
                    -1,
                )
        ) {
            is Failure -> assertTrue(result.value is UpdateCharacteristicsError.InvalidCharacteristics)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `update characteristics - athlete not found`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (
            val result =
                athleteServices.updateCharacteristics(
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
                    1,
                )
        ) {
            is Failure -> assertTrue(result.value is UpdateCharacteristicsError.AthleteNotFound)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `update characteristics - not athlete's coach`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (
            val result =
                athleteServices.updateCharacteristics(
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
                    1,
                )
        ) {
            is Failure -> assertTrue(result.value is UpdateCharacteristicsError.NotAthletesCoach)
            is Success -> fail("Unexpected $result")
        }
    }

    /**
     * Remove Characteristics Tests
     */

    @Test
    fun `remove characteristics - success`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (
            val result =
                athleteServices.removeCharacteristics(
                    FIRST_COACH_ID,
                    FIRST_ATHLETE_ID,
                    ATHLETE_CHARACTERISTICS_SECOND_DATE,
                )
        ) {
            is Failure -> fail("Unexpected $result")
            is Success -> assertTrue(result.value == FIRST_ATHLETE_ID)
        }
    }

    @Test
    fun `remove characteristics - invalid date`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        val invalidDates =
            listOf(
                "32-01-2000",
                "2000-01-01",
                "01-01-2200",
            )

        invalidDates.forEach { date ->
            when (
                val result =
                    athleteServices.removeCharacteristics(
                        FIRST_COACH_ID,
                        FIRST_ATHLETE_ID,
                        date,
                    )
            ) {
                is Failure -> assertTrue(result.value is RemoveCharacteristicsError.InvalidDate)
                is Success -> fail("Unexpected $result")
            }
        }
    }

    @Test
    fun `remove characteristics - athlete not found`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (
            val result =
                athleteServices.removeCharacteristics(
                    FIRST_COACH_ID,
                    0,
                    ATHLETE_CHARACTERISTICS_FIRST_DATE,
                )
        ) {
            is Failure -> assertTrue(result.value is RemoveCharacteristicsError.AthleteNotFound)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `remove characteristics - characteristics not found`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (
            val result =
                athleteServices.removeCharacteristics(
                    FIRST_COACH_ID,
                    FIRST_ATHLETE_ID,
                    VALID_DATE,
                )
        ) {
            is Failure -> assertTrue(result.value is RemoveCharacteristicsError.CharacteristicsNotFound)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `remove characteristics - not athlete's coach`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (
            val result =
                athleteServices.removeCharacteristics(
                    SECOND_COACH_ID,
                    FIRST_ATHLETE_ID,
                    ATHLETE_CHARACTERISTICS_FIRST_DATE,
                )
        ) {
            is Failure -> assertTrue(result.value is RemoveCharacteristicsError.NotAthletesCoach)
            is Success -> fail("Unexpected $result")
        }
    }

    /**
     * Set Calendar Tests
     */

    // TODO: Implement set calendar tests

    /**
     * Get calendar
     */

    @Test
    fun `get calendar - success`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (val result = athleteServices.getCalendar(FIRST_COACH_ID, FIRST_ATHLETE_ID, null)) {
            is Failure -> fail("Unexpected $result")
            is Success -> assertTrue(result.value.isNotEmpty())
        }

        when (val result = athleteServices.getCalendar(FIRST_COACH_ID, FIRST_ATHLETE_ID, "invalid")) {
            is Failure -> fail("Unexpected $result")
            is Success -> assertTrue(result.value.isNotEmpty())
        }
    }

    @Test
    fun `get calendar - success (only gym activity)`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (val result = athleteServices.getCalendar(FIRST_COACH_ID, FIRST_ATHLETE_ID, "gym")) {
            is Failure -> fail("Unexpected $result")
            is Success -> {
                assertTrue(
                    result.value.all { mesocycle ->
                        mesocycle.microcycles.all { microcycle ->
                            microcycle.activities.all { it.type == ActivityType.GYM }
                        }
                    },
                )
            }
        }
    }

    @Test
    fun `get calendar - success (only water activity)`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (val result = athleteServices.getCalendar(FIRST_COACH_ID, FIRST_ATHLETE_ID, "water")) {
            is Failure -> fail("Unexpected $result")
            is Success -> {
                assertTrue(
                    result.value.all { mesocycle ->
                        mesocycle.microcycles.all { microcycle ->
                            microcycle.activities.all { it.type == ActivityType.WATER }
                        }
                    },
                )
            }
        }
    }

    @Test
    fun `get calendar - athlete not found`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (val result = athleteServices.getCalendar(FIRST_COACH_ID, 0, null)) {
            is Failure -> assertTrue(result.value is GetCalendarError.AthleteNotFound)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `get calendar - not athlete's coach`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (val result = athleteServices.getCalendar(SECOND_COACH_ID, FIRST_ATHLETE_ID, null)) {
            is Failure -> assertTrue(result.value is GetCalendarError.NotAthletesCoach)
            is Success -> fail("Unexpected $result")
        }
    }

    /**
     * Get Water Activities Tests
     */

    @Test
    fun `get water activities - success`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (val result = athleteServices.getWaterActivities(FIRST_COACH_ID, FIRST_ATHLETE_ID)) {
            is Failure -> fail("Unexpected $result")
            is Success -> assertTrue(result.value.isNotEmpty())
        }

        when (val result = athleteServices.getWaterActivities(FIRST_ATHLETE_ID, FIRST_ATHLETE_ID)) {
            is Failure -> fail("Unexpected $result")
            is Success -> assertTrue(result.value.isNotEmpty())
        }
    }

    @Test
    fun `get water activities - athlete not found`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (val result = athleteServices.getWaterActivities(FIRST_COACH_ID, 0)) {
            is Failure -> assertTrue(result.value is GetWaterActivitiesError.AthleteNotFound)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `get water activities - not athlete's coach`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (val result = athleteServices.getWaterActivities(SECOND_COACH_ID, FIRST_ATHLETE_ID)) {
            is Failure -> assertTrue(result.value is GetWaterActivitiesError.NotAthletesCoach)
            is Success -> fail("Unexpected $result")
        }
    }

    /**
     * CREATE COMPETITION TESTS
     */

    @Test
    fun `create competition - success`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (
            val result =
                athleteServices.createCompetition(
                    FIRST_COACH_ID,
                    FIRST_ATHLETE_ID,
                    DATE,
                    "Location",
                    2,
                    emptyList()
                )
        ) {
            is Failure -> fail("Unexpected $result")
            is Success -> assertTrue(result.value > 0)
        }
    }

    @Test
    fun `create competition - invalid date`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        val invalidDates =
            listOf(
                "32-01-2000",
                "2000-01-01",
            )

        invalidDates.forEach { date ->
            when (
                val result =
                    athleteServices.createCompetition(
                        FIRST_COACH_ID,
                        FIRST_ATHLETE_ID,
                        date,
                        "Location",
                        1,
                        emptyList()
                    )
            ) {
                is Failure -> assertTrue(result.value is CreateCompetitionError.InvalidDate)
                is Success -> fail("Unexpected $result")
            }
        }
    }

    @Test
    fun `create competition - athlete not found`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (
            val result =
                athleteServices.createCompetition(
                    FIRST_COACH_ID,
                    0,
                    DATE,
                    "Location",
                    1,
                    emptyList()
                )
        ) {
            is Failure -> assertTrue(result.value is CreateCompetitionError.AthleteNotFound)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `create competition - not athlete's coach`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (
            val result =
                athleteServices.createCompetition(
                    SECOND_COACH_ID,
                    FIRST_ATHLETE_ID,
                    DATE,
                    "Location",
                    2,
                    emptyList()
                )
        ) {
            is Failure -> assertTrue(result.value is CreateCompetitionError.NotAthletesCoach)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `create competition - activity without microcycle`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (
            val result =
                athleteServices.createCompetition(
                    FIRST_COACH_ID,
                    FIRST_ATHLETE_ID,
                    VALID_DATE,
                    randomString(),
                    2,
                    listOf(
                        HeatInputInfo(
                            score = 2f,
                            waterActivity = WaterActivityInputInfo(
                                athleteId = FIRST_ATHLETE_ID,
                                rpe = 4,
                                condition = randomString(),
                                trimp = 2,
                                duration = 60,
                                waves = listOf(
                                    WaveInputInfo(
                                        points = null,
                                        rightSide = true,
                                        maneuvers = listOf(
                                            ManeuverInputInfo(
                                                waterManeuverId = 1,
                                                success = true
                                            ),
                                        )
                                    ),
                                ),
                            ),
                        ),
                    )
                )
        ) {
            is Failure -> assertTrue(result.value is CreateCompetitionError.ActivityWithoutMicrocycle)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `create competition - invalid duration`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (
            val result =
                athleteServices.createCompetition(
                    FIRST_COACH_ID,
                    FIRST_ATHLETE_ID,
                    DATE,
                    randomString(),
                    2,
                    listOf(
                        HeatInputInfo(
                            score = 2f,
                            waterActivity = WaterActivityInputInfo(
                                athleteId = FIRST_ATHLETE_ID,
                                rpe = 4,
                                condition = randomString(),
                                trimp = 2,
                                duration = -60, // Invalid duration
                                waves = listOf(
                                    WaveInputInfo(
                                        points = null,
                                        rightSide = true,
                                        maneuvers = listOf(
                                            ManeuverInputInfo(
                                                waterManeuverId = 1,
                                                success = true
                                            ),
                                        )
                                    ),
                                ),
                            ),
                        ),
                    )
                )
        ) {
            is Failure -> assertTrue(result.value is CreateCompetitionError.InvalidDuration)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `create competition - invalid rpe`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (
            val result =
                athleteServices.createCompetition(
                    FIRST_COACH_ID,
                    FIRST_ATHLETE_ID,
                    DATE,
                    randomString(),
                    2,
                    listOf(
                        HeatInputInfo(
                            score = 2f,
                            waterActivity = WaterActivityInputInfo(
                                athleteId = FIRST_ATHLETE_ID,
                                rpe = -1, // Invalid RPE
                                condition = randomString(),
                                trimp = 2,
                                duration = 60,
                                waves = listOf(
                                    WaveInputInfo(
                                        points = null,
                                        rightSide = true,
                                        maneuvers = listOf(
                                            ManeuverInputInfo(
                                                waterManeuverId = 1,
                                                success = true
                                            ),
                                        )
                                    ),
                                ),
                            ),
                        ),
                    )
                )
        ) {
            is Failure -> assertTrue(result.value is CreateCompetitionError.InvalidRpe)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `create competition - invalid trimp`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (
            val result =
                athleteServices.createCompetition(
                    FIRST_COACH_ID,
                    FIRST_ATHLETE_ID,
                    DATE,
                    randomString(),
                    2,
                    listOf(
                        HeatInputInfo(
                            score = 2f,
                            waterActivity = WaterActivityInputInfo(
                                athleteId = FIRST_ATHLETE_ID,
                                rpe = 4,
                                condition = randomString(),
                                trimp = -1, // Invalid TRIMP
                                duration = 60,
                                waves = listOf(
                                    WaveInputInfo(
                                        points = null,
                                        rightSide = true,
                                        maneuvers = listOf(
                                            ManeuverInputInfo(
                                                waterManeuverId = 1,
                                                success = true
                                            ),
                                        )
                                    ),
                                ),
                            ),
                        ),
                    )
                )
        ) {
            is Failure -> assertTrue(result.value is CreateCompetitionError.InvalidTrimp)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `create competition - invalid score`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (
            val result =
                athleteServices.createCompetition(
                    FIRST_COACH_ID,
                    FIRST_ATHLETE_ID,
                    VALID_DATE,
                    randomString(),
                    2,
                    listOf(
                        HeatInputInfo(
                            score = -1f, // Invalid score
                            waterActivity = WaterActivityInputInfo(
                                athleteId = FIRST_ATHLETE_ID,
                                rpe = 4,
                                condition = randomString(),
                                trimp = 2,
                                duration = 60,
                                waves = listOf(
                                    WaveInputInfo(
                                        points = null,
                                        rightSide = true,
                                        maneuvers = listOf(
                                            ManeuverInputInfo(
                                                waterManeuverId = 1,
                                                success = true
                                            ),
                                        )
                                    ),
                                ),
                            ),
                        ),
                    )
                )
        ) {
            is Failure -> assertTrue(result.value is CreateCompetitionError.InvalidScore)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `create competition - invalid water maneuver`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (
            val result =
                athleteServices.createCompetition(
                    FIRST_COACH_ID,
                    FIRST_ATHLETE_ID,
                    DATE,
                    randomString(),
                    2,
                    listOf(
                        HeatInputInfo(
                            score = 2f,
                            waterActivity = WaterActivityInputInfo(
                                athleteId = FIRST_ATHLETE_ID,
                                rpe = 4,
                                condition = randomString(),
                                trimp = 2,
                                duration = 60,
                                waves = listOf(
                                    WaveInputInfo(
                                        points = null,
                                        rightSide = true,
                                        maneuvers = listOf(
                                            ManeuverInputInfo(
                                                waterManeuverId = 0, // Invalid water maneuver ID
                                                success = true
                                            ),
                                        )
                                    ),
                                ),
                            ),
                        ),
                    )
                )
        ) {
            is Failure -> assertTrue(result.value is CreateCompetitionError.InvalidWaterManeuver)
            is Success -> fail("Unexpected $result")
        }
    }

    /**
     * Get Competition Tests
     */

    @Test
    fun `get competition - success`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (
            val result =
                athleteServices.getCompetition(FIRST_COACH_ID, FIRST_ATHLETE_ID, FIRST_COMPETITION_ID)
        ) {
            is Failure -> fail("Unexpected $result")
            is Success -> assertTrue(result.value.id == FIRST_COMPETITION_ID)
        }
    }

    @Test
    fun `get competition - athlete not found`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (
            val result =
                athleteServices.getCompetition(FIRST_COACH_ID, 0, FIRST_COMPETITION_ID)
        ) {
            is Failure -> assertTrue(result.value is GetCompetitionError.AthleteNotFound)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `get competition - not athlete's coach`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (
            val result =
                athleteServices.getCompetition(SECOND_COACH_ID, FIRST_ATHLETE_ID, FIRST_COMPETITION_ID)
        ) {
            is Failure -> assertTrue(result.value is GetCompetitionError.NotAthletesCoach)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `get competition - competition not found`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (
            val result =
                athleteServices.getCompetition(FIRST_COACH_ID, FIRST_ATHLETE_ID, 0)
        ) {
            is Failure -> assertTrue(result.value is GetCompetitionError.CompetitionNotFound)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `get competition - not athlete's competition`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (
            val result =
                athleteServices.getCompetition(SECOND_COACH_ID, FOURTH_ATHLETE_ID, FIRST_COMPETITION_ID)
        ) {
            is Failure -> assertTrue(result.value is GetCompetitionError.NotAthletesCompetition)
            is Success -> fail("Unexpected $result")
        }
    }

    /**
     * Remove Competition Tests
     */

    @Test
    fun `remove competition - success`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (
            val result =
                athleteServices.removeCompetition(FIRST_COACH_ID, FIRST_ATHLETE_ID, SECOND_COMPETITION_ID)
        ) {
            is Failure -> fail("Unexpected $result")
            is Success -> assertTrue(result.value == SECOND_COMPETITION_ID)
        }
    }

    @Test
    fun `remove competition - athlete not found`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (
            val result =
                athleteServices.removeCompetition(FIRST_COACH_ID, 0, FIRST_COMPETITION_ID)
        ) {
            is Failure -> assertTrue(result.value is RemoveCompetitionError.AthleteNotFound)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `remove competition - not athlete's coach`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (
            val result =
                athleteServices.removeCompetition(SECOND_COACH_ID, FIRST_ATHLETE_ID, SECOND_COMPETITION_ID)
        ) {
            is Failure -> assertTrue(result.value is RemoveCompetitionError.NotAthletesCoach)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `remove competition - competition not found`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (
            val result =
                athleteServices.removeCompetition(FIRST_COACH_ID, FIRST_ATHLETE_ID, 0)
        ) {
            is Failure -> assertTrue(result.value is RemoveCompetitionError.CompetitionNotFound)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `remove competition - not athlete's competition`() {
        val testClock = TestClock()
        val athleteServices = createAthleteServices(testClock, maxTokensPerUser = MAX_TOKENS_PER_USER)

        when (
            val result =
                athleteServices.removeCompetition(SECOND_COACH_ID, FOURTH_ATHLETE_ID, FIRST_COMPETITION_ID)
        ) {
            is Failure -> assertTrue(result.value is RemoveCompetitionError.NotAthletesCompetition)
            is Success -> fail("Unexpected $result")
        }
    }

    companion object {
        private fun randomString() = "String_${abs(Random.nextLong())}"

        private const val DATE = "03-05-2025"
        private const val VALID_DATE = "01-01-2000"
        private const val FIRST_COACH_ID = 1
        private const val SECOND_COACH_ID = 2

        private const val FIRST_ATHLETE_ID = 3
        private const val FIRST_ATHLETE_USERNAME = "athlete"
        private const val FIRST_ATHLETE_NAME = "John Doe"
        private const val FIRST_ATHLETE_BIRTH_DATE: Long = 631152000

        private const val SECOND_ATHLETE_ID = 4

        private const val THIRD_ATHLETE_ID = 5
        private const val THIRD_ATHLETE_USERNAME = "athlete3"

        private const val FOURTH_ATHLETE_ID = 6

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

        private const val FIRST_COMPETITION_ID = 1
        private const val SECOND_COMPETITION_ID = 2

        private const val MAX_TOKENS_PER_USER = 5

        private val EXERCISES_LIST =
            listOf(
                ExerciseInputInfo(
                    sets =
                        listOf(
                            SetInputInfo(
                                reps = 10,
                                weight = 60f,
                                restTime = 60f,
                            ),
                        ),
                    gymExerciseId = 1,
                ),
            )

        private fun createAthleteServices(
            testClock: TestClock,
            tokenTtl: Duration = 30.days,
            tokenRollingTtl: Duration = 30.minutes,
            maxTokensPerUser: Int,
        ) = AthleteServices(
            JdbiTransactionManager(jdbi),
            AthleteDomain(Sha256TokenEncoder()),
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
            ActivityDomain(),
            WaterActivityDomain(),
            CloudinaryServices(cloudinary),
            testClock,
        )

        private val jdbi =
            Jdbi.create(
                PGSimpleDataSource().apply {
                    setURL("jdbc:postgresql://localhost:5432/db?user=dbuser&password=changeit")
                },
            ).configureWithAppRequirements()

        private val cloudinary =
            Cloudinary(
                mapOf(
                    "cloud_name" to "",
                    "api_key" to "",
                    "api_secret" to "",
                ),
            )
    }
}

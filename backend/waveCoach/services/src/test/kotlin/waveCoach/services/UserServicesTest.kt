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
import kotlin.test.assertEquals
import kotlin.test.fail
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

class UserServicesTest {

    /**
     * Create User Tests
     */

    @Test
    fun `create user - success`() {
        val testClock = TestClock()
        val maxTokensPerUser = 5
        val userService = createUserServices(testClock, maxTokensPerUser = maxTokensPerUser)

        val username = randomString()
        val password = randomString()

        val result = userService.createUser(username, password)
        when (result) {
            is Failure -> fail("Unexpected $result")
            is Success -> assertTrue(result.value > 1)
        }
    }

    @Test
    fun `create user - insecure password`() {
        val testClock = TestClock()
        val maxTokensPerUser = 5
        val userService = createUserServices(testClock, maxTokensPerUser = maxTokensPerUser)

        val insecurePasswords = listOf(
            "Abc1234", // missing special character
            "abc123!", // missing uppercase letter
            "ABC123!", // missing lowercase letter
            "Abc!@#", // missing number
            "Abc123", // smaller than 6 characters
        )

        insecurePasswords.forEach { password ->
            val result = userService.createUser(randomString(), password)
            when (result) {
                is Failure -> assertTrue(result.value is CreateUserError.InsecurePassword)
                is Success -> fail("Unexpected $result")
            }
        }
    }

    @Test
    fun `create user - username already exists`() {
        val testClock = TestClock()
        val maxTokensPerUser = 5
        val userService = createUserServices(testClock, maxTokensPerUser = maxTokensPerUser)

        val username = USERNAME_OF_ADMIN
        val password = randomString()

        val result = userService.createUser(username, password)
        when (result) {
            is Failure -> assertTrue(result.value is CreateUserError.UsernameAlreadyExists)
            is Success -> fail("Unexpected $result")
        }
    }

    /**
     * Login Tests
     */

    @Test
    fun `login - success`() {
        val testClock = TestClock()
        val userService = createUserServices(testClock)

        val username = USERNAME_OF_ADMIN
        val password = PASSWORD_OF_ADMIN

        val result = userService.login(username, password)
        when (result) {
            is Failure -> fail("Unexpected $result")
            is Success -> {
                assertEquals(ID_OF_ADMIN, result.value.id)
                assertEquals(username, result.value.username)
                assertTrue(result.value.tokenValue.isNotEmpty())
            }
        }
    }

    @Test
    fun `login - username is blank`() {
        val testClock = TestClock()
        val userService = createUserServices(testClock)

        val username = ""
        val password = PASSWORD_OF_ADMIN

        val result = userService.login(username, password)
        when (result) {
            is Failure -> assertTrue(result.value is LoginError.UsernameIsBlank)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `login - password is blank`() {
        val testClock = TestClock()
        val userService = createUserServices(testClock)

        val username = USERNAME_OF_ADMIN
        val password = ""

        val result = userService.login(username, password)
        when (result) {
            is Failure -> assertTrue(result.value is LoginError.PasswordIsBlank)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `login - invalid login`() {
        val testClock = TestClock()
        val userService = createUserServices(testClock)

        val invalidLogins = listOf(
            USERNAME_OF_ADMIN to randomString(),
            randomString() to PASSWORD_OF_ADMIN,
            randomString() to randomString(),
        )

        invalidLogins.forEach { (username, password) ->
            val result = userService.login(username, password)
            when (result) {
                is Failure -> assertTrue(result.value is LoginError.InvalidLogin)
                is Success -> fail("Unexpected $result")
            }
        }
    }

    /**
     * GetUserByToken Tests
     */

    @Test
    fun `get user by token - success`() {
        val testClock = TestClock()
        val userService = createUserServices(testClock)

        val result = userService.getUserByToken(TOKEN_OF_ADMIN)
        when (result) {
            is Failure -> fail("Unexpected $result")
            is Success -> {
                assertEquals(ID_OF_ADMIN, result.value.id)
                assertEquals(USERNAME_OF_ADMIN, result.value.username)
            }
        }
    }

    @Test
    fun `get user by token - invalid token`() {
        val testClock = TestClock()
        val userService = createUserServices(testClock)

        val invalidTokens = listOf(
            "",
            "invalid",
            randomString(),
        )

        invalidTokens.forEach { token ->
            val result = userService.getUserByToken(token)
            when (result) {
                is Failure -> assertTrue(result.value is GetUserByTokenError.InvalidToken)
                is Success -> fail("Unexpected $result")
            }
        }
    }

    companion object {
        val ID_OF_ADMIN = 1
        val USERNAME_OF_ADMIN = "admin"
        val PASSWORD_OF_ADMIN = "Admin123!"
        val TOKEN_OF_ADMIN = "i_aY-4lpMqAIMuhkimTbKy4xYEuyvgFPaaTpVS0lctQ="

        private fun createUserServices(
            testClock: TestClock,
            tokenTtl: Duration = 30.days,
            tokenRollingTtl: Duration = 30.minutes,
            maxTokensPerUser: Int = 3,
        ) = UserServices(
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
            testClock,
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
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
     * checkCredentials Tests
     */

    @Test
    fun `checkCredentials - success`() {
        val testClock = TestClock()
        val userService = createUserServices(testClock)

        val username = USERNAME_TO_LOGIN
        val password = PASSWORD_TO_LOGIN

        val result = userService.checkCredentials(username, password)
        when (result) {
            is Failure -> fail("Unexpected $result")
            is Success -> {
                assertEquals(ID_OF_THIRD_COACH, result.value.id)
                assertEquals(username, result.value.username)
                assertTrue(result.value.tokenValue.isNotEmpty())
            }
        }
    }

    @Test
    fun `checkCredentials - username is blank`() {
        val testClock = TestClock()
        val userService = createUserServices(testClock)

        val username = ""
        val password = PASSWORD_OF_ADMIN

        val result = userService.checkCredentials(username, password)
        when (result) {
            is Failure -> assertTrue(result.value is CheckCredentialsError.UsernameIsBlank)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `checkCredentials - password is blank`() {
        val testClock = TestClock()
        val userService = createUserServices(testClock)

        val username = USERNAME_OF_ADMIN
        val password = ""

        val result = userService.checkCredentials(username, password)
        when (result) {
            is Failure -> assertTrue(result.value is CheckCredentialsError.PasswordIsBlank)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `checkCredentials - invalid login`() {
        val testClock = TestClock()
        val userService = createUserServices(testClock)

        val invalidLogins =
            listOf(
                USERNAME_OF_ADMIN to randomString(),
                randomString() to PASSWORD_OF_ADMIN,
                randomString() to randomString(),
            )

        invalidLogins.forEach { (username, password) ->
            val result = userService.checkCredentials(username, password)
            when (result) {
                is Failure -> assertTrue(result.value is CheckCredentialsError.InvalidLogin)
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

        val result = userService.getUserByToken(TOKEN_OF_THIRD_COACH)
        when (result) {
            is Failure -> fail("Unexpected $result")
            is Success -> {
                assertEquals(ID_OF_THIRD_COACH, result.value.id)
                assertEquals(USERNAME_TO_LOGIN, result.value.username)
            }
        }
    }

    @Test
    fun `get user by token - invalid token`() {
        val testClock = TestClock()
        val userService = createUserServices(testClock)

        val invalidTokens =
            listOf(
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

    /**
     * RevokeToken Tests
     */

    @Test
    fun `revoke token - success`() {
        val testClock = TestClock()
        val userService = createUserServices(testClock)

        val token = (userService.checkCredentials(USERNAME_OF_ADMIN, PASSWORD_OF_ADMIN) as Success).value.tokenValue

        val result = userService.getUserByToken(token)
        when (result) {
            is Failure -> fail("Unexpected $result")
            is Success -> {
                assertEquals(ID_OF_ADMIN, result.value.id)
                assertEquals(USERNAME_OF_ADMIN, result.value.username)
            }
        }

        userService.revokeToken(token)

        val result2 = userService.getUserByToken(token)
        when (result2) {
            is Failure -> assertTrue(result2.value is GetUserByTokenError.TokenNotFound)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `revoke token - invalid token`() {
        val testClock = TestClock()
        val userService = createUserServices(testClock)

        val invalidTokens =
            listOf(
                "",
                "invalid",
                randomString(),
            )

        invalidTokens.forEach { token ->
            val result = userService.revokeToken(token)
            when (result) {
                is Failure -> assertTrue(result.value is RevokeTokenError.InvalidToken)
                is Success -> fail("Unexpected $result")
            }
        }
    }

    @Test
    fun `revoke token - token not found`() {
        val testClock = TestClock()
        val userService = createUserServices(testClock)

        val token = (userService.checkCredentials(USERNAME_OF_ADMIN, PASSWORD_OF_ADMIN) as Success).value.tokenValue

        val result = userService.getUserByToken(token)
        when (result) {
            is Failure -> fail("Unexpected $result")
            is Success -> {
                assertEquals(ID_OF_ADMIN, result.value.id)
                assertEquals(USERNAME_OF_ADMIN, result.value.username)
            }
        }

        userService.revokeToken(token)

        val result2 = userService.revokeToken(token)
        when (result2) {
            is Failure -> assertTrue(result2.value is RevokeTokenError.TokenNotFound)
            is Success -> fail("Unexpected $result")
        }
    }

    /**
     * UpdateCredentials Tests
     */

    @Test
    fun `update credentials - success`() {
        val testClock = TestClock()
        val userService = createUserServices(testClock)

        val newUsername = randomString()
        val newPassword = randomString()

        val result = userService.updateCredentials(ID_OF_SECOND_COACH, newUsername, newPassword)
        when (result) {
            is Failure -> fail("Unexpected $result")
            is Success -> {
                // No assertion here, just checking that the update was successful
            }
        }
    }

    @Test
    fun `update credentials - invalid username`() {
        val testClock = TestClock()
        val userService = createUserServices(testClock)

        val newUsername = ""
        val newPassword = randomString()

        val result = userService.updateCredentials(ID_OF_SECOND_COACH, newUsername, newPassword)
        when (result) {
            is Failure -> assertTrue(result.value is UserUpdateError.InvalidUsername)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `update credentials - insecure password`() {
        val testClock = TestClock()
        val userService = createUserServices(testClock)

        val insecurePasswords =
            listOf(
                "Abc1234", // missing special character
                "abc123!", // missing uppercase letter
                "ABC123!", // missing lowercase letter
                "Abc!@#", // missing number
                "Abc123", // smaller than 6 characters
            )

        insecurePasswords.forEach { password ->
            val newUsername = randomString()

            val result = userService.updateCredentials(ID_OF_SECOND_COACH, newUsername, password)
            when (result) {
                is Failure -> assertTrue(result.value is UserUpdateError.InsecurePassword)
                is Success -> fail("Unexpected $result")
            }
        }
    }

    @Test
    fun `update credentials - username already exists`() {
        val testClock = TestClock()
        val userService = createUserServices(testClock)

        val newUsername = USERNAME_OF_ADMIN
        val newPassword = randomString()

        val result = userService.updateCredentials(ID_OF_SECOND_COACH, newUsername, newPassword)
        when (result) {
            is Failure -> assertTrue(result.value is UserUpdateError.UsernameAlreadyExists)
            is Success -> fail("Unexpected $result")
        }
    }

    companion object {
        private val ID_OF_ADMIN = 1
        private val USERNAME_OF_ADMIN = "admin"
        private val PASSWORD_OF_ADMIN = "Admin123!"
        private val TOKEN_OF_ADMIN = "i_aY-4lpMqAIMuhkimTbKy4xYEuyvgFPaaTpVS0lctQ="

        private val ID_OF_SECOND_COACH = 2
        private val USERNAME_OF_SECOND_COACH = "user2"
        private val TOKEN_OF_SECOND_COACH = "fM5JjtPOUqtnZg1lB7jnJhXBP5gI2WbIIBoO3JhYM5M="


        private val ID_OF_THIRD_COACH = 8
        private val USERNAME_TO_LOGIN = "user3"
        private val PASSWORD_TO_LOGIN = "Admin123!"
        private val TOKEN_OF_THIRD_COACH = "jxuHflgoufWumSnj3IWryNxPEZHusJwgBPbeFhU8784="

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

        private val jdbi =
            Jdbi.create(
                PGSimpleDataSource().apply {
                    setURL("jdbc:postgresql://localhost:5432/db?user=dbuser&password=changeit")
                },
            ).configureWithAppRequirements()
    }
}

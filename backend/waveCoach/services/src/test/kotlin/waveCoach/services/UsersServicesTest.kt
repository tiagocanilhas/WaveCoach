package pt.isel.daw.imSystem.services

import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.Assertions.assertTrue
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import pt.isel.daw.imSystem.domain.Sha256TokenEncoder
import pt.isel.daw.imSystem.domain.UsersDomain
import pt.isel.daw.imSystem.domain.UsersDomainConfig
import pt.isel.daw.imSystem.repository.jdbi.JdbiTransactionManager
import pt.isel.daw.imSystem.repository.jdbi.configureWithAppRequirements
import pt.isel.daw.imSystem.utils.Either
import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

class UsersServicesTest {
    @Test
    fun `create user`() {
        val testClock = TestClock()
        val maxTokensPerUser = 5
        val userService = createUsersService(testClock, maxTokensPerUser = maxTokensPerUser)

        val username = randomString()
        val password = randomString()
        val appInvite = (userService.createAppInvite(uidOfAdminOnDb) as Either.Right).value

        when (val createUserResult = userService.createUser(username, password, appInvite)) {
            is Either.Left -> fail("Unexpected $createUserResult")
            is Either.Right -> assertTrue(createUserResult.value > 0)
        }
    }

    @Test
    fun `create user - insecure password bigger than 6`() {
        val testClock = TestClock()
        val maxTokensPerUser = 5
        val userService = createUsersService(testClock, maxTokensPerUser = maxTokensPerUser)

        val username = randomString()
        val password = "Abc12!"
        val appInvite = (userService.createAppInvite(uidOfAdminOnDb) as Either.Right).value

        when (val createUserResult = userService.createUser(username, password, appInvite)) {
            is Either.Left -> assertTrue(createUserResult.value is UserCreationError.InsecurePassword)
            is Either.Right -> fail("Unexpected $createUserResult")
        }
    }

    @Test
    fun `create user - insecure password no uppercase`() {
        val testClock = TestClock()
        val maxTokensPerUser = 5
        val userService = createUsersService(testClock, maxTokensPerUser = maxTokensPerUser)

        val username = randomString()
        val password = "abc123!"
        val appInvite = (userService.createAppInvite(uidOfAdminOnDb) as Either.Right).value

        when (val createUserResult = userService.createUser(username, password, appInvite)) {
            is Either.Left -> assertTrue(createUserResult.value is UserCreationError.InsecurePassword)
            is Either.Right -> fail("Unexpected $createUserResult")
        }
    }

    @Test
    fun `create user - insecure password no lowercase`() {
        val testClock = TestClock()
        val maxTokensPerUser = 5
        val userService = createUsersService(testClock, maxTokensPerUser = maxTokensPerUser)

        val username = randomString()
        val password = "ABC123!"
        val appInvite = (userService.createAppInvite(uidOfAdminOnDb) as Either.Right).value

        when (val createUserResult = userService.createUser(username, password, appInvite)) {
            is Either.Left -> assertTrue(createUserResult.value is UserCreationError.InsecurePassword)
            is Either.Right -> fail("Unexpected $createUserResult")
        }
    }

    @Test
    fun `create user - insecure password no digit`() {
        val testClock = TestClock()
        val maxTokensPerUser = 5
        val userService = createUsersService(testClock, maxTokensPerUser = maxTokensPerUser)

        val username = randomString()
        val password = "Abcdef!"
        val appInvite = (userService.createAppInvite(uidOfAdminOnDb) as Either.Right).value

        when (val createUserResult = userService.createUser(username, password, appInvite)) {
            is Either.Left -> assertTrue(createUserResult.value is UserCreationError.InsecurePassword)
            is Either.Right -> fail("Unexpected $createUserResult")
        }
    }

    @Test
    fun `create user - insecure password no special char`() {
        val testClock = TestClock()
        val maxTokensPerUser = 5
        val userService = createUsersService(testClock, maxTokensPerUser = maxTokensPerUser)

        val username = randomString()
        val password = "Abc1234"
        val appInvite = (userService.createAppInvite(uidOfAdminOnDb) as Either.Right).value

        when (val createUserResult = userService.createUser(username, password, appInvite)) {
            is Either.Left -> assertTrue(createUserResult.value is UserCreationError.InsecurePassword)
            is Either.Right -> fail("Unexpected $createUserResult")
        }
    }

    @Test
    fun `create user - invalid app invite`() {
        val testClock = TestClock()
        val maxTokensPerUser = 5
        val userService = createUsersService(testClock, maxTokensPerUser = maxTokensPerUser)

        val username = randomString()
        val password = randomString()
        val appInvite = "invalid App Invite"

        when (val createUserResult = userService.createUser(username, password, appInvite)) {
            is Either.Left -> assertTrue(createUserResult.value is UserCreationError.InvalidAppInvite)
            is Either.Right -> fail("Unexpected $createUserResult")
        }
    }

    @Test
    fun `create user - using the same app invite`() {
        val testClock = TestClock()
        val maxTokensPerUser = 5
        val userService = createUsersService(testClock, maxTokensPerUser = maxTokensPerUser)

        val username = randomString()
        val password = randomString()
        val appInvite = (userService.createAppInvite(uidOfAdminOnDb) as Either.Right).value

        when (val createUserResult = userService.createUser(username, password, appInvite)) {
            is Either.Left -> fail("Unexpected $createUserResult")
            is Either.Right -> assertTrue(createUserResult.value > 0)
        }

        val username2 = randomString()
        when (val createUserResult2 = userService.createUser(username2, password, appInvite)) {
            is Either.Left -> assertTrue(createUserResult2.value is UserCreationError.InvalidAppInvite)
            is Either.Right -> fail("Unexpected $createUserResult2")
        }
    }

    @Test
    fun `create user - user already exists`() {
        val testClock = TestClock()
        val maxTokensPerUser = 5
        val userService = createUsersService(testClock, maxTokensPerUser = maxTokensPerUser)

        val username = randomString()
        val password = randomString()
        val appInvite = (userService.createAppInvite(uidOfAdminOnDb) as Either.Right).value

        when (val createUserResult = userService.createUser(username, password, appInvite)) {
            is Either.Left -> fail("Unexpected $createUserResult")
            is Either.Right -> assertTrue(createUserResult.value > 0)
        }

        val appInvite2 = (userService.createAppInvite(uidOfAdminOnDb) as Either.Right).value
        when (val createUserResult2 = userService.createUser(username, password, appInvite2)) {
            is Either.Left -> assertTrue(createUserResult2.value is UserCreationError.UserAlreadyExists)
            is Either.Right -> fail("Unexpected $createUserResult2")
        }
    }

    @Test
    fun `get user by id`() {
        val testClock = TestClock()
        val maxTokensPerUser = 5
        val userService = createUsersService(testClock, maxTokensPerUser = maxTokensPerUser)

        when (val getUserResult = userService.getUserById(uidOfAdminOnDb)) {
            is Either.Left -> fail("Unexpected $getUserResult")
            is Either.Right -> {
                val user = getUserResult.value
                assertEquals(uidOfAdminOnDb, user.id)
                assertEquals(usernameOfAdminOnDb, user.username)
                assertTrue(passwordEncoder.matches(passwordOfAdminOnDb, user.password.value))
            }
        }
    }

    @Test
    fun `get user by id - user not found`() {
        val testClock = TestClock()
        val maxTokensPerUser = 5
        val userService = createUsersService(testClock, maxTokensPerUser = maxTokensPerUser)

        when (val getUserResult = userService.getUserById(Random.nextInt())) {
            is Either.Left -> assertTrue(getUserResult.value is GetUserError.UserNotFound)
            is Either.Right -> fail("Unexpected $getUserResult")
        }
    }

    @Test
    fun `get users`() {
        val testClock = TestClock()
        val maxTokensPerUser = 5
        val userService = createUsersService(testClock, maxTokensPerUser = maxTokensPerUser)

        val users = userService.getUsers("", null, LIMIT, SKIP)
        when (val usersResult = users) {
            is Either.Left -> fail("Unexpected $usersResult")
            is Either.Right -> {
                val usersList = usersResult.value
                assertTrue(usersList.isNotEmpty())
            }
        }
    }

    @Test
    fun `get users - empty list`() {
        val testClock = TestClock()
        val maxTokensPerUser = 5
        val userService = createUsersService(testClock, maxTokensPerUser = maxTokensPerUser)
        val username = randomString()
        val users = userService.getUsers(username, null, LIMIT, SKIP)
        when (val usersResult = users) {
            is Either.Left -> fail("Unexpected $usersResult")
            is Either.Right -> {
                val usersList = usersResult.value
                assertTrue(usersList.isEmpty())
            }
        }
    }

    @Test
    fun `get users specific name`() {
        val testClock = TestClock()
        val maxTokensPerUser = 5
        val userService = createUsersService(testClock, maxTokensPerUser = maxTokensPerUser)
        val username = "admin"
        val users = userService.getUsers(username, null, LIMIT, SKIP)
        when (val usersResult = users) {
            is Either.Left -> fail("Unexpected $usersResult")
            is Either.Right -> {
                val usersList = usersResult.value
                assertTrue(usersList.size == 1)
            }
        }
    }

    @Test
    fun `get users relative name`() {
        val testClock = TestClock()
        val maxTokensPerUser = 5
        val userService = createUsersService(testClock, maxTokensPerUser = maxTokensPerUser)
        val username = "User"
        val users = userService.getUsers(username, null, LIMIT, SKIP)
        when (val usersResult = users) {
            is Either.Left -> fail("Unexpected $usersResult")
            is Either.Right -> {
                val usersList = usersResult.value
                assertTrue(usersList.isNotEmpty())
            }
        }
    }

    @Test
    fun `get user channels`() {
        val testClock = TestClock()
        val maxTokensPerUser = 5
        val userService = createUsersService(testClock, maxTokensPerUser = maxTokensPerUser)

        when (val userChannels = userService.getUserChannels(uidOfAdminOnDb, LIMIT, SKIP)) {
            is Either.Left -> fail("Unexpected $userChannels")
            is Either.Right -> {
                val channels = userChannels.value.first
                assertTrue(channels.isNotEmpty())
            }
        }
    }

    @Test
    fun `get user channels - user not found`() {
        val testClock = TestClock()
        val maxTokensPerUser = 5
        val userService = createUsersService(testClock, maxTokensPerUser = maxTokensPerUser)

        when (val userChannels = userService.getUserChannels(Random.nextInt(), LIMIT, SKIP)) {
            is Either.Left -> assertTrue(userChannels.value is GetUserChannelsError.UserNotFound)
            is Either.Right -> fail("Unexpected $userChannels")
        }
    }

    @Test
    fun `create token`() {
        val testClock = TestClock()
        val maxTokensPerUser = 5
        val userService = createUsersService(testClock, maxTokensPerUser = maxTokensPerUser)

        when (val token = userService.createToken(usernameOfAdminOnDb, passwordOfAdminOnDb)) {
            is Either.Left -> fail("Unexpected $token")
            is Either.Right -> {
                val tokenExternalInfo = token.value
                assertEquals(44, tokenExternalInfo.tokenValue.length)
            }
        }
    }

    @Test
    fun `create token - username invalid`() {
        val testClock = TestClock()
        val maxTokensPerUser = 5
        val userService = createUsersService(testClock, maxTokensPerUser = maxTokensPerUser)
        val username = ""

        when (val token = userService.createToken(username, passwordOfAdminOnDb)) {
            is Either.Left -> assertTrue(token.value is TokenCreationError.UsernameIsInvalid)
            is Either.Right -> fail("Unexpected $token")
        }
    }

    @Test
    fun `create token - password invalid`() {
        val testClock = TestClock()
        val maxTokensPerUser = 5
        val userService = createUsersService(testClock, maxTokensPerUser = maxTokensPerUser)
        val password = ""

        when (val token = userService.createToken(usernameOfAdminOnDb, password)) {
            is Either.Left -> assertTrue(token.value is TokenCreationError.PasswordIsInvalid)
            is Either.Right -> fail("Unexpected $token")
        }
    }

    @Test
    fun `create token - invalid login`() {
        val testClock = TestClock()
        val maxTokensPerUser = 5
        val userService = createUsersService(testClock, maxTokensPerUser = maxTokensPerUser)
        val username1 = randomString()
        val password1 = randomString()

        when (val token = userService.createToken(username1, password1)) {
            is Either.Left -> assertTrue(token.value is TokenCreationError.InvalidLogin)
            is Either.Right -> fail("Unexpected $token")
        }
    }

    @Test
    fun `get user by token - success`() {
        val testClock = TestClock()
        val maxTokensPerUser = 5
        val usersService = createUsersService(testClock, maxTokensPerUser = maxTokensPerUser)

        when (val getUserResult = usersService.getUserByToken(tokenOfAdminOnDb)) {
            is Either.Left -> fail("Unexpected ${getUserResult.value}")
            is Either.Right -> {
                assertEquals(uidOfAdminOnDb, getUserResult.value.id)
                assertEquals(usernameOfAdminOnDb, getUserResult.value.username)
                assertTrue(passwordEncoder.matches(passwordOfAdminOnDb, getUserResult.value.password.value))
            }
        }
    }

    @Test
    fun `get user by token - invalid token`() {
        val testClock = TestClock()
        val maxTokensPerUser = 5
        val usersService = createUsersService(testClock, maxTokensPerUser = maxTokensPerUser)

        val token = "invalidToken"
        when (val getUserResult = usersService.getUserByToken(token)) {
            is Either.Left -> assertTrue(getUserResult.value is GetUserByTokenError.InvalidToken)
            is Either.Right -> fail("Unexpected $getUserResult")
        }
    }

    @Test
    fun `get user by token - token not found`() {
        val testClock = TestClock()
        val maxTokensPerUser = 5
        val usersService = createUsersService(testClock, maxTokensPerUser = maxTokensPerUser)

        when (val getUserResult = usersService.getUserByToken(randomToken)) {
            is Either.Left -> assertTrue(getUserResult.value is GetUserByTokenError.TokenNotFound)
            is Either.Right -> fail("Unexpected $getUserResult")
        }
    }

    @Test
    fun `revoke token - success`() {
        val testClock = TestClock()
        val maxTokensPerUser = 5
        val userService = createUsersService(testClock, maxTokensPerUser = maxTokensPerUser)

        val token = (userService.createToken(usernameOfAdminOnDb, passwordOfAdminOnDb) as Either.Right).value.tokenValue

        when (val revokeResult = userService.revokeToken(token)) {
            is Either.Left -> fail("Failed to revoke token: ${revokeResult.value}")
            is Either.Right -> assertTrue(revokeResult.value)
        }

        when (val getUserResult = userService.getUserByToken(token)) {
            is Either.Left -> assertTrue(getUserResult.value is GetUserByTokenError.TokenNotFound)
            is Either.Right -> fail("Token should have been revoked")
        }
    }

    @Test
    fun `revoke token - invalid token`() {
        val testClock = TestClock()
        val maxTokensPerUser = 5
        val usersService = createUsersService(testClock, maxTokensPerUser = maxTokensPerUser)

        val invalidToken = "invalidToken"
        when (val revokeResult = usersService.revokeToken(invalidToken)) {
            is Either.Left -> assertTrue(revokeResult.value is RevokeTokenError.InvalidToken)
            is Either.Right -> fail("Unexpected $revokeResult")
        }
    }

    @Test
    fun `revoke token - token not found`() {
        val testClock = TestClock()
        val maxTokensPerUser = 5
        val userService = createUsersService(testClock, maxTokensPerUser = maxTokensPerUser)

        val token = (userService.createToken(usernameOfAdminOnDb, passwordOfAdminOnDb) as Either.Right).value.tokenValue

        when (val revokeResult = userService.revokeToken(token)) {
            is Either.Left -> fail("Failed to revoke token: ${revokeResult.value}")
            is Either.Right -> assertTrue(revokeResult.value)
        }

        when (val revokeResult2 = userService.revokeToken(token)) {
            is Either.Left -> assertTrue(revokeResult2.value is RevokeTokenError.TokenNotFound)
            is Either.Right -> fail("Unexpected $revokeResult2")
        }
    }

    @Test
    fun `get user channel invites - success`() {
        val testClock = TestClock()
        val maxTokensPerUser = 5
        val usersService = createUsersService(testClock, maxTokensPerUser = maxTokensPerUser)

        when (val result = usersService.getUserChannelInvites(uidOfUser4OnDB, LIMIT, SKIP)) {
            is Either.Left -> fail("Unexpected ${result.value}")
            is Either.Right -> {
                assertTrue(result.value.first.isNotEmpty())
            }
        }
    }

    @Test
    fun `get user channel invites - user not found`() {
        val testClock = TestClock()
        val maxTokensPerUser = 5
        val usersService = createUsersService(testClock, maxTokensPerUser = maxTokensPerUser)
        val uid = Random.nextInt()

        when (val result = usersService.getUserChannelInvites(uid, LIMIT, SKIP)) {
            is Either.Left -> assertTrue(result.value is GetChannelsInvitesError.UserNotFound)
            is Either.Right -> fail("Unexpected $result")
        }
    }

    companion object {
        val uidOfAdminOnDb = 1
        val usernameOfAdminOnDb = "admin"
        val passwordOfAdminOnDb = "Admin123!"
        val tokenOfAdminOnDb = "i_aY-4lpMqAIMuhkimTbKy4xYEuyvgFPaaTpVS0lctQ="

        val uidOfUser2OnDB = 2

        val tokenOfUser2OnDB = "fM5JjtPOUqtnZg1lB7jnJhXBP5gI2WbIIBoO3JhYM5M="

        val randomToken = "RM5JcdPOUqtnZg1lB73mJeXBP5zI2WbIIBoO3JhYM5M="

        val uidOfUser4OnDB = 4

        private fun createUsersService(
            testClock: TestClock,
            tokenTtl: Duration = 30.days,
            tokenRollingTtl: Duration = 30.minutes,
            maxTokensPerUser: Int = 3,
        ) = UsersService(
            JdbiTransactionManager(jdbi),
            UsersDomain(
                BCryptPasswordEncoder(),
                Sha256TokenEncoder(),
                UsersDomainConfig(
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
package pt.isel.daw.imSystem.repository

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.fail
import pt.isel.daw.imSystem.domain.AppInvite
import pt.isel.daw.imSystem.domain.PasswordValidationInfo
import pt.isel.daw.imSystem.domain.Token
import pt.isel.daw.imSystem.domain.TokenValidationInfo
import pt.isel.daw.imSystem.domain.Type
import pt.isel.daw.imSystem.repository.jdbi.JdbiChannelsRepository
import pt.isel.daw.imSystem.repository.jdbi.JdbiUsersRepository
import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class JdbiUsersRepositoryTest {
    @Test
    fun `store user`() = testWithHandleAndRollback { handle ->
        val usersRepository = JdbiUsersRepository(handle)
        val username = randomString()
        val password = randomString()

        val uid = usersRepository.storeUser(username, PasswordValidationInfo(password))

        assertNotNull(uid)
    }

    @Test
    fun `get user by id`() = testWithHandleAndRollback { handle ->
        val usersRepository = JdbiUsersRepository(handle)

        val user = usersRepository.getUserById(UID)

        assertEquals(UID, user?.id)
        assertEquals(USERNAME, user?.username)
        assertEquals(PasswordValidationInfo(PASSWORD), user?.password)
    }

    @Test
    fun `get user by id - null`() = testWithHandleAndRollback { handle ->
        val usersRepository = JdbiUsersRepository(handle)

        val uid = Random.nextInt()
        val user = usersRepository.getUserById(uid)

        assertNull(user)
    }

    @Test
    fun `is user stored by id`() = testWithHandleAndRollback { handle ->
        val usersRepository = JdbiUsersRepository(handle)

        val isUserStored = usersRepository.isUserStoredById(UID)

        val uid2 = abs(Random.nextInt())
        val isUserStored2 = usersRepository.isUserStoredById(uid2)

        assertTrue(isUserStored)
        assertFalse(isUserStored2)
    }

    @Test
    fun `get users`() = testWithHandleAndRollback { handle ->
        val usersRepository = JdbiUsersRepository(handle)

        val users = usersRepository.getUsers("", null, LIMIT, SKIP)

        assertTrue(users.isNotEmpty())
    }

    @Test
    fun `get users - empty`() = testWithHandleAndRollback { handle ->
        val usersRepository = JdbiUsersRepository(handle)

        val username = randomString()
        val users = usersRepository.getUsers(username, null, LIMIT, SKIP)

        assertTrue(users.isEmpty())
    }

    @Test
    fun `get user specific name`() = testWithHandleAndRollback { handle ->
        val usersRepository = JdbiUsersRepository(handle)

        val users = usersRepository.getUsers(USERNAME, null, LIMIT, SKIP)

        assertTrue(users.isNotEmpty())
    }

    @Test
    fun `get user relative name`() = testWithHandleAndRollback { handle ->
        val usersRepository = JdbiUsersRepository(handle)

        val username = "User"
        val users = usersRepository.getUsers(username, null, LIMIT, SKIP)

        assertTrue(users.isNotEmpty())
    }

    @Test
    fun `get user by username`() = testWithHandleAndRollback { handle ->
        val usersRepository = JdbiUsersRepository(handle)
        val user = usersRepository.getUserByUsername(USERNAME)

        assertEquals(UID, user?.id)
        assertEquals(USERNAME, user?.username)
        assertEquals(PasswordValidationInfo(PASSWORD), user?.password)
    }

    @Test
    fun `get user by username - null`() = testWithHandleAndRollback { handle ->
        val usersRepository = JdbiUsersRepository(handle)

        val username = randomString()
        val user = usersRepository.getUserByUsername(username)

        assertNull(user)
    }

    @Test
    fun `is user stored by username`() = testWithHandleAndRollback { handle ->
        val usersRepository = JdbiUsersRepository(handle)

        val isUserStored = usersRepository.isUserStoredByUsername(USERNAME)

        val username2 = randomString()
        val isUserStored2 = usersRepository.isUserStoredByUsername(username2)

        assertTrue(isUserStored)
        assertFalse(isUserStored2)
    }

    @Test
    fun `get token by token validation info`() = testWithHandleAndRollback { handle ->
        val usersRepository = JdbiUsersRepository(handle)
        val clock = TestClock()

        val tokenValue = randomString()
        val tokenValidationInfo = TokenValidationInfo(tokenValue)
        val tokenCreationInstant = clock.now()
        val token = Token(tokenValidationInfo, UID, tokenCreationInstant, tokenCreationInstant)
        usersRepository.storeToken(token, 1)

        val (user, tokenFromDb) = usersRepository.getTokenByTokenValidationInfo(token.tokenValidationInfo)
            ?: fail("token and user must be not null")

        assertEquals(UID, user.id)
        assertEquals(USERNAME, user.username)
        assertEquals(PasswordValidationInfo(PASSWORD), user.password)

        assertEquals(tokenValidationInfo, tokenFromDb.tokenValidationInfo)
        assertEquals(UID, tokenFromDb.uid)
        assertEquals(tokenCreationInstant, tokenFromDb.createdAt)
        assertEquals(tokenCreationInstant, tokenFromDb.lastUsedAt)
    }

    @Test
    fun `get token by token validation info - null`() = testWithHandleAndRollback { handle ->
        val usersRepository = JdbiUsersRepository(handle)

        val tokenValue = randomString()
        val tokenValidationInfo = TokenValidationInfo(tokenValue)
        val userAndToken = usersRepository.getTokenByTokenValidationInfo(tokenValidationInfo)

        assertNull(userAndToken)
    }

    @Test
    fun `update token last used`() = testWithHandleAndRollback { handle ->
        val usersRepository = JdbiUsersRepository(handle)
        val clock = TestClock()

        val tokenValue = randomString()
        val tokenValidationInfo = TokenValidationInfo(tokenValue)
        val tokenCreationInstant = clock.now()
        val token = Token(tokenValidationInfo, UID, tokenCreationInstant, tokenCreationInstant)
        usersRepository.storeToken(token, 1)

        val newLastUsedAt = clock.now()
        usersRepository.updateTokenLastUsed(token, newLastUsedAt)

        val (_, tokenFromDb) = usersRepository.getTokenByTokenValidationInfo(token.tokenValidationInfo)
            ?: fail("token and user must be not null")

        assertEquals(newLastUsedAt, tokenFromDb.lastUsedAt)
    }

    @Test
    fun `remove token by token validation info`() = testWithHandleAndRollback { handle ->
        val usersRepository = JdbiUsersRepository(handle)
        val clock = TestClock()

        val tokenValue = randomString()
        val tokenValidationInfo = TokenValidationInfo(tokenValue)
        val tokenCreationInstant = clock.now()
        val token = Token(tokenValidationInfo, UID, tokenCreationInstant, tokenCreationInstant)
        usersRepository.storeToken(token, 1)

        usersRepository.removeTokenByTokenValidationInfo(token.tokenValidationInfo)

        assertNull(usersRepository.getTokenByTokenValidationInfo(token.tokenValidationInfo))
    }

    @Test
    fun `get user channel invites`() = testWithHandleAndRollback { handle ->
        val usersRepository = JdbiUsersRepository(handle)
        val channelsRepository = JdbiChannelsRepository(handle)

        val channelname = randomString()
        val channeldesc = randomString()
        val cid = channelsRepository.storeChannel(UID, channelname, channeldesc, true)

        val type = Type.MEMBER
        channelsRepository.inviteUserToChannel(UID, cid, type)

        val (invites, hasNext) = usersRepository.getUserChannelInvites(UID, LIMIT, SKIP)

        assertTrue(invites.isNotEmpty())
    }

    @Test
    fun `get user channel invites - empty`() = testWithHandleAndRollback { handle ->
        val usersRepository = JdbiUsersRepository(handle)

        val uid = abs(Random.nextInt())
        val (invites, hasNext) = usersRepository.getUserChannelInvites(uid, LIMIT, SKIP)

        assertTrue(invites.isEmpty())
    }

    @Test
    fun `is app invite valid`() = testWithHandleAndRollback { handle ->
        val usersRepository = JdbiUsersRepository(handle)

        val appInvite = AppInvite(randomString())
        usersRepository.storeAppinvite(appInvite, UID)

        val isValid = usersRepository.isAppInviteValid(appInvite)

        assertTrue(isValid)
    }

    @Test
    fun `remove app invite`() = testWithHandleAndRollback { handle ->
        val usersRepository = JdbiUsersRepository(handle)

        val appInvite = AppInvite(randomString())
        usersRepository.storeAppinvite(appInvite, UID)

        usersRepository.removeAppInvite(appInvite)

        assertFalse(usersRepository.isAppInviteValid(appInvite))
    }

    companion object {
        private fun randomString() = "string_${abs(Random.nextLong())}"

        private const val LIMIT = 10
        private const val SKIP = 0

        private const val UID = 1
        private const val USERNAME = "admin"
        private const val PASSWORD = "\$2a\$10\$6wZjBBzU2G1H7rK5VeFNIu.toyRHL.ULP1R2IMjpPy7xB8HhF/h0q"
    }
}
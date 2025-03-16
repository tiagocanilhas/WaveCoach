package waveCoach.repository.jdbi

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.postgresql.util.PSQLException
import waveCoach.domain.PasswordValidationInfo
import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.Test

class JdbiUserRepositoryTest {

    /**
     * Store User Tests
     */

    @Test
    fun `store user`() = testWithHandleAndRollback { handle ->
        val userRepository = JdbiUserRepository(handle)
        val username = randomString()
        val password = randomString()

        val uid = userRepository.storeUser(username, PasswordValidationInfo(password))

        assertNotNull(uid)
    }

    @Test
    fun `store user with existing username`() = testWithHandleAndRollback { handle ->
        val userRepository = JdbiUserRepository(handle)

        try {
            userRepository.storeUser(USERNAME, PasswordValidationInfo(PASSWORD))
        } catch (e: PSQLException) {
            // Expected
        }
    }

    @Test
    fun `remove user`() = testWithHandleAndRollback { handle ->
        val userRepository = JdbiUserRepository(handle)

        val username = randomString()
        val uid = userRepository.storeUser(username, PasswordValidationInfo(PASSWORD))
        userRepository.removeUser(uid)

        val user = userRepository.getUserByUsername(username)

        assertNull(user)
    }

    /**
     * Check Username Tests
     */

    @Test
    fun `check username`() = testWithHandleAndRollback { handle ->
        val userRepository = JdbiUserRepository(handle)

        val exists = userRepository.checkUsername(USERNAME)

        assertNotNull(exists)
    }

    companion object {
        private fun randomString() = "String_${abs(Random.nextLong())}"

        private const val UID = 1
        private const val USERNAME = "admin"
        private const val PASSWORD = "\$2a\$10\$6wZjBBzU2G1H7rK5VeFNIu.toyRHL.ULP1R2IMjpPy7xB8HhF/h0q"
    }
}
package waveCoach.repository.jdbi

import org.junit.jupiter.api.Assertions.assertNotNull
import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.Test

class JdbiUsersRepositoryTest {
    @Test
    fun `store user`() = testWithHandleAndRollback { handle ->
        val usersRepository = JdbiUserRepository(handle)
        val username = randomString()
        val password = randomString()

        val uid = 1 //usersRepository.storeUser(username, PasswordValidationInfo(password))

        assertNotNull(uid)
    }

    companion object {
        private fun randomString() = "string_${abs(Random.nextLong())}"

        private const val UID = 1
        private const val USERNAME = "admin"
        private const val PASSWORD = "\$2a\$10\$6wZjBBzU2G1H7rK5VeFNIu.toyRHL.ULP1R2IMjpPy7xB8HhF/h0q"
    }
}
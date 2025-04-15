package waveCoach.repository.jdbi

import org.jdbi.v3.core.statement.UnableToExecuteStatementException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.postgresql.util.PSQLException
import waveCoach.domain.PasswordValidationInfo
import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.Test

class JdbiCoachRepositoryTest {
    /**
     * Store Coach Tests
     */

    @Test
    fun `store coach`() =
        testWithHandleAndRollback { handle ->
            val userRepository = JdbiUserRepository(handle)
            val coachRepository = JdbiCoachRepository(handle)

            val username = randomString()
            val password = randomString()
            val uid = userRepository.storeUser(username, PasswordValidationInfo(password))
            val cid = coachRepository.storeCoach(uid)

            assertEquals(uid, cid)
        }

    @Test
    fun `store coach with existing uid`() =
        testWithHandleAndRollback { handle ->
            val coachRepository = JdbiCoachRepository(handle)

            try {
                coachRepository.storeCoach(UID)
            } catch (e: UnableToExecuteStatementException) {
                assertTrue(e.cause is PSQLException)
                assertTrue(e.message!!.contains("duplicate key value violates unique constraint"))
            }
        }

    companion object {
        private fun randomString() = "String_${abs(Random.nextLong())}"

        private const val UID = 1
    }
}

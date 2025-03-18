package waveCoach.repository.jdbi

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import waveCoach.domain.PasswordValidationInfo
import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.Test

class JdbiAthleteRepositoryTest {

    @Test
    fun `store athlete`() = testWithHandleAndRollback { handle ->
        val athleteRepository = JdbiAthleteRepository(handle)
        val userRepository = JdbiUserRepository(handle)

        val uid = userRepository.storeUser(USERNAME, PasswordValidationInfo(PASSWORD))

        val name = randomString()
        val aid = athleteRepository.storeAthlete(uid, COACH_ID, name, BIRTHDATE)

        assertNotNull(aid)
    }

    @Test
    fun `get athlete`() = testWithHandleAndRollback { handle ->
        val athleteRepository = JdbiAthleteRepository(handle)
        val userRepository = JdbiUserRepository(handle)

        val uid = userRepository.storeUser(USERNAME, PasswordValidationInfo(PASSWORD))

        val name = randomString()
        val aid = athleteRepository.storeAthlete(uid, COACH_ID, name, BIRTHDATE)

        val athlete = athleteRepository.getAthlete(aid)

        assertNotNull(athlete)
    }

    @Test
    fun `remove athlete`() = testWithHandleAndRollback { handle ->
        val athleteRepository = JdbiAthleteRepository(handle)
        val userRepository = JdbiUserRepository(handle)

        val uid = userRepository.storeUser(USERNAME, PasswordValidationInfo(PASSWORD))

        val name = randomString()
        val aid = athleteRepository.storeAthlete(uid, COACH_ID, name, BIRTHDATE)

        athleteRepository.removeAthlete(aid)

        val athlete = athleteRepository.getAthlete(aid)

        assertNull(athlete)
    }

    companion object {
        private fun randomString() = "String_${abs(Random.nextLong())}"

        private const val BIRTHDATE = 958815600000 // (15-05-2000)
        private const val COACH_ID = 1
        private const val USERNAME = "athlete test"
        private const val PASSWORD = "changeit"
    }
}
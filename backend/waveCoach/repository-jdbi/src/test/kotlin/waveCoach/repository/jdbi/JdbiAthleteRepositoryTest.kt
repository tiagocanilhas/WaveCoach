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
    fun `get athlete list`() = testWithHandleAndRollback { handle ->
        val athleteRepository = JdbiAthleteRepository(handle)

        val athletes = athleteRepository.getAthleteList(COACH_ID)

        assert(athletes.isNotEmpty())
    }

    @Test
    fun `update athlete`() = testWithHandleAndRollback { handle ->
        val athleteRepository = JdbiAthleteRepository(handle)

        val updatedName = randomString()
        athleteRepository.updateAthlete(ATHLETE_ID, updatedName, BIRTHDATE)

        val athlete = athleteRepository.getAthlete(ATHLETE_ID)

        assertNotNull(athlete)
        assert(athlete!!.name == updatedName)
    }

    @Test
    fun `remove athlete`() = testWithHandleAndRollback { handle ->
        val athleteRepository = JdbiAthleteRepository(handle)
        val userRepository = JdbiUserRepository(handle)
        val characteristicsRepository = JdbiCharacteristicsRepository(handle)

        val uid = userRepository.storeUser(USERNAME, PasswordValidationInfo(PASSWORD))

        val name = randomString()
        val aid = athleteRepository.storeAthlete(uid, COACH_ID, name, BIRTHDATE)

        athleteRepository.removeAthlete(aid)

        val athlete = athleteRepository.getAthlete(aid)

        assertNull(athlete)

        val characteristics = characteristicsRepository.getCharacteristicsList(aid)

        assert(characteristics.isEmpty())
    }

    companion object {
        private fun randomString() = "String_${abs(Random.nextLong())}"

        private const val BIRTHDATE = 958815600000 // (15-05-2000)
        private const val COACH_ID = 1
        private const val USERNAME = "athlete test"
        private const val PASSWORD = "changeit"
        private const val ATHLETE_ID = 3
    }
}
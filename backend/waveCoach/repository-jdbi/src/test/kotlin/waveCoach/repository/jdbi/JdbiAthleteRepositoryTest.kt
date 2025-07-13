package waveCoach.repository.jdbi

import kotlinx.datetime.Clock
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import waveCoach.domain.AthleteCode
import waveCoach.domain.CodeValidationInfo
import waveCoach.domain.PasswordValidationInfo
import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class JdbiAthleteRepositoryTest {
    @Test
    fun `store athlete`() =
        testWithHandleAndRollback { handle ->
            val athleteRepository = JdbiAthleteRepository(handle)
            val userRepository = JdbiUserRepository(handle)

            val uid = userRepository.storeUser(USERNAME, PasswordValidationInfo(PASSWORD))

            val name = randomString()
            val aid = athleteRepository.storeAthlete(uid, COACH_ID, name, birthdate, null)

            assertNotNull(aid)
        }

    @Test
    fun `get athlete`() =
        testWithHandleAndRollback { handle ->
            val athleteRepository = JdbiAthleteRepository(handle)
            val userRepository = JdbiUserRepository(handle)

            val uid = userRepository.storeUser(USERNAME, PasswordValidationInfo(PASSWORD))

            val name = randomString()
            val aid = athleteRepository.storeAthlete(uid, COACH_ID, name, birthdate, null)

            val athlete = athleteRepository.getAthlete(aid)

            assertNotNull(athlete)
        }

    @Test
    fun `get athlete list`() =
        testWithHandleAndRollback { handle ->
            val athleteRepository = JdbiAthleteRepository(handle)

            val athletes = athleteRepository.getAthleteList(COACH_ID)

            assert(athletes.isNotEmpty())
        }

    @Test
    fun `update athlete`() =
        testWithHandleAndRollback { handle ->
            val athleteRepository = JdbiAthleteRepository(handle)

            val updatedName = randomString()
            athleteRepository.updateAthlete(ATHLETE_ID, updatedName, birthdate, null)

            val athlete = athleteRepository.getAthlete(ATHLETE_ID)

            assertNotNull(athlete)
            assert(athlete!!.name == updatedName)
        }

    @Test
    fun `remove athlete`() =
        testWithHandleAndRollback { handle ->
            val athleteRepository = JdbiAthleteRepository(handle)
            val userRepository = JdbiUserRepository(handle)
            val characteristicsRepository = JdbiCharacteristicsRepository(handle)

            val uid = userRepository.storeUser(USERNAME, PasswordValidationInfo(PASSWORD))

            val name = randomString()
            val aid = athleteRepository.storeAthlete(uid, COACH_ID, name, birthdate, null)

            athleteRepository.removeAthlete(aid)

            val athlete = athleteRepository.getAthlete(aid)

            assertNull(athlete)

            val characteristics = characteristicsRepository.getCharacteristicsList(aid)

            assert(characteristics.isEmpty())
        }

    @Test
    fun `store code`() =
        testWithHandleAndRollback { handle ->
            val athleteRepository = JdbiAthleteRepository(handle)

            val code =
                AthleteCode(
                    ATHLETE_ID,
                    CodeValidationInfo(randomString()),
                    Clock.System.now(),
                )

            athleteRepository.storeCode(code)
        }

    @Test
    fun `get by code`() =
        testWithHandleAndRollback { handle ->
            val athleteRepository = JdbiAthleteRepository(handle)

            val (athlete, username, createdTime) = athleteRepository.getByCode(CodeValidationInfo(FIRST_ATHLETE_CODE))!!

            assertEquals(ATHLETE_ID, athlete.uid)
            assertEquals(FIRST_ATHLETE_USERNAME, username)
            assertTrue(createdTime > 0)
        }

    companion object {
        private fun randomString() = "String_${abs(Random.nextLong())}"

        private const val birthdate = 958815600000 // (15-05-2000)
        private const val COACH_ID = 1
        private const val USERNAME = "athlete test"
        private const val PASSWORD = "changeit"

        private const val ATHLETE_ID = 3
        private const val FIRST_ATHLETE_USERNAME = "athlete"
        private const val FIRST_ATHLETE_CODE = "mGi_ziXh1QK26wim_2Eq3fTbK-2UYOQwGat8keR69EA="
    }
}

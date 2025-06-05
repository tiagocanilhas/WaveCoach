package waveCoach.repository.jdbi

import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.Test

class JdbiCompetitionRepositoryTest {
    @Test
    fun `store competition`() =
        testWithHandleAndRollback { handle ->
            val competitionRepository = JdbiCompetitionRepository(handle)

            val competitionId = competitionRepository.storeCompetition(
                uid = UID,
                date = DATE,
                location = randomString()
            )

            val competition = competitionRepository.competitionExists(competitionId)

            assertTrue(competitionId > 0)

            assertTrue(competition)
        }

    companion object {
        private fun randomString() = "String_${abs(Random.nextLong())}"

        private const val UID = 3
        private const val DATE = 1620000000000
    }
}
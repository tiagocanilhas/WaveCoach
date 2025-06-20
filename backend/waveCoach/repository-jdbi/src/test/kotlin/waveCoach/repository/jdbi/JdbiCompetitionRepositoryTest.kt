package waveCoach.repository.jdbi

import org.junit.jupiter.api.Assertions.assertTrue
import waveCoach.domain.HeatToInsert
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
                location = randomString(),
                place = 1
            )

            val competition = competitionRepository.competitionExists(competitionId)

            assertTrue(competitionId > 0)

            assertTrue(competition)
        }

    @Test
    fun `get competition`() =
        testWithHandleAndRollback { handle ->
            val competitionRepository = JdbiCompetitionRepository(handle)

            val competitionId = competitionRepository.storeCompetition(
                uid = UID,
                date = DATE,
                location = randomString(),
                place = 1
            )

            val competition = competitionRepository.getCompetition(competitionId)

            assertTrue(competition != null)
            assertTrue(competition!!.id == competitionId)
            assertTrue(competition.uid == UID)
            assertTrue(competition.date == DATE)
            assertTrue(competition.location.isNotEmpty())
            assertTrue(competition.place == 1)
        }

    @Test
    fun `remove competition`() =
        testWithHandleAndRollback { handle ->
            val competitionRepository = JdbiCompetitionRepository(handle)

            val competitionId = competitionRepository.storeCompetition(
                uid = UID,
                date = DATE,
                location = randomString(),
                place = 1
            )

            competitionRepository.removeCompetition(competitionId)

            val competitionExists = competitionRepository.competitionExists(competitionId)

            assertTrue(!competitionExists)
        }

    @Test
    fun `store heats`() =
        testWithHandleAndRollback { handle ->
            val competitionRepository = JdbiCompetitionRepository(handle)
            val activityRepository = JdbiActivityRepository(handle)
            val waterActivityRepository = JdbiWaterActivityRepository(handle)

            val competitionId = competitionRepository.storeCompetition(
                uid = UID,
                date = DATE,
                location = randomString(),
                place = 1
            )
            val activityId = activityRepository.storeActivity(UID, ANOTHER_DATE, MICRO_ID)

            val waterActivityId = waterActivityRepository.storeWaterActivity(
                activityId,
                3,
                randomString(),
                4,
                60,
            )

            val heatToInsert = HeatToInsert(
                competitionId = competitionId,
                waterActivityId = waterActivityId,
                score = 2f
            )

            val heatIds = competitionRepository.storeHeats(listOf(heatToInsert))

            assertTrue(heatIds.isNotEmpty())
            assertTrue(heatIds[0] > 0)
        }

    companion object {
        private fun randomString() = "String_${abs(Random.nextLong())}"

        private const val UID = 3
        private const val DATE = 1620000000000
        private const val ANOTHER_DATE = 1743801600000 // (02-08-2025)
        private const val MICRO_ID = 4
    }
}
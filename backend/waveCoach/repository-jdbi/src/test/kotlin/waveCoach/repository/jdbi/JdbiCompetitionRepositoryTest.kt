package waveCoach.repository.jdbi

import org.junit.jupiter.api.Assertions.assertTrue
import waveCoach.domain.HeatToInsert
import waveCoach.domain.HeatToUpdate
import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.Test

class JdbiCompetitionRepositoryTest {
    @Test
    fun `store competition`() =
        testWithHandleAndRollback { handle ->
            val competitionRepository = JdbiCompetitionRepository(handle)

            val competitionId =
                competitionRepository.storeCompetition(
                    uid = UID,
                    date = DATE,
                    location = randomString(),
                    place = 1,
                    name = randomString(),
                )

            val competition = competitionRepository.competitionExists(competitionId)

            assertTrue(competitionId > 0)

            assertTrue(competition)
        }

    @Test
    fun `get competition`() =
        testWithHandleAndRollback { handle ->
            val competitionRepository = JdbiCompetitionRepository(handle)

            val competitionId =
                competitionRepository.storeCompetition(
                    uid = UID,
                    date = DATE,
                    location = randomString(),
                    place = 1,
                    name = randomString(),
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
    fun `get competitions by athlete`() =
        testWithHandleAndRollback { handle ->
            val competitionRepository = JdbiCompetitionRepository(handle)

            val competitionId1 =
                competitionRepository.storeCompetition(
                    uid = UID,
                    date = DATE,
                    location = randomString(),
                    place = 1,
                    name = randomString(),
                )
            val competitionId2 =
                competitionRepository.storeCompetition(
                    uid = UID,
                    date = DATE + 1000,
                    location = randomString(),
                    place = 2,
                    name = randomString(),
                )

            val competitions = competitionRepository.getCompetitionsByAthlete(UID)

            assertTrue(competitions.isNotEmpty())
            assertTrue(competitions.any { it.id == competitionId1 && it.date == DATE })
            assertTrue(competitions.any { it.id == competitionId2 && it.date == DATE + 1000 })
        }

    @Test
    fun `update competition`() =
        testWithHandleAndRollback { handle ->
            val competitionRepository = JdbiCompetitionRepository(handle)

            val competitionId =
                competitionRepository.storeCompetition(
                    uid = UID,
                    date = DATE,
                    location = randomString(),
                    place = 1,
                    name = randomString(),
                )

            val newLocation = randomString()
            val newPlace = 2

            competitionRepository.updateCompetition(
                id = competitionId,
                date = null,
                location = newLocation,
                place = newPlace,
                name = null,
            )

            val updatedCompetition = competitionRepository.getCompetition(competitionId)

            assertTrue(updatedCompetition != null)
            assertTrue(updatedCompetition!!.id == competitionId)
            assertTrue(updatedCompetition.uid == UID)
            assertTrue(updatedCompetition.date == DATE)
            assertTrue(updatedCompetition.location == newLocation)
            assertTrue(updatedCompetition.place == newPlace)
        }

    @Test
    fun `remove competition`() =
        testWithHandleAndRollback { handle ->
            val competitionRepository = JdbiCompetitionRepository(handle)

            val competitionId =
                competitionRepository.storeCompetition(
                    uid = UID,
                    date = DATE,
                    location = randomString(),
                    place = 1,
                    name = randomString(),
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

            val competitionId =
                competitionRepository.storeCompetition(
                    uid = UID,
                    date = DATE,
                    location = randomString(),
                    place = 1,
                    name = randomString(),
                )
            val activityId = activityRepository.storeActivity(UID, ANOTHER_DATE, MICRO_ID)

            val waterActivityId =
                waterActivityRepository.storeWaterActivity(
                    activityId,
                    3,
                    randomString(),
                    4,
                    60,
                )

            val heatToInsert =
                HeatToInsert(
                    competitionId = competitionId,
                    waterActivityId = waterActivityId,
                    score = 2,
                )

            val heatIds = competitionRepository.storeHeats(listOf(heatToInsert))

            assertTrue(heatIds.isNotEmpty())
            assertTrue(heatIds[0] > 0)
        }

    @Test
    fun `get heats by competition`() =
        testWithHandleAndRollback { handle ->
            val competitionRepository = JdbiCompetitionRepository(handle)
            val activityRepository = JdbiActivityRepository(handle)
            val waterActivityRepository = JdbiWaterActivityRepository(handle)

            val competitionId =
                competitionRepository.storeCompetition(
                    uid = UID,
                    date = DATE,
                    location = randomString(),
                    place = 1,
                    name = randomString(),
                )
            val activityId = activityRepository.storeActivity(UID, ANOTHER_DATE, MICRO_ID)

            val waterActivityId =
                waterActivityRepository.storeWaterActivity(
                    activityId,
                    3,
                    randomString(),
                    4,
                    60,
                )

            val heatToInsert =
                HeatToInsert(
                    competitionId = competitionId,
                    waterActivityId = waterActivityId,
                    score = 2,
                )

            competitionRepository.storeHeats(listOf(heatToInsert))

            val heats = competitionRepository.getHeatsByCompetition(competitionId)

            assertTrue(heats.isNotEmpty())
            assertTrue(heats[0].competition == competitionId)
        }

    @Test
    fun `update heats`() =
        testWithHandleAndRollback { handle ->
            val competitionRepository = JdbiCompetitionRepository(handle)
            val activityRepository = JdbiActivityRepository(handle)
            val waterActivityRepository = JdbiWaterActivityRepository(handle)

            val competitionId =
                competitionRepository.storeCompetition(
                    uid = UID,
                    date = DATE,
                    location = randomString(),
                    place = 1,
                    name = randomString(),
                )
            val activityId = activityRepository.storeActivity(UID, ANOTHER_DATE, MICRO_ID)

            val waterActivityId =
                waterActivityRepository.storeWaterActivity(
                    activityId,
                    3,
                    randomString(),
                    4,
                    60,
                )

            val heatToInsert =
                HeatToInsert(
                    competitionId = competitionId,
                    waterActivityId = waterActivityId,
                    score = 2,
                )

            val heatIds = competitionRepository.storeHeats(listOf(heatToInsert))

            assertTrue(heatIds.isNotEmpty())
            assertTrue(heatIds[0] > 0)

            // Update the heat
            val updatedHeatToInsert = HeatToUpdate(heatIds[0], 3)
            competitionRepository.updateHeats(listOf(updatedHeatToInsert))

            // Verify the update
            val heats = competitionRepository.getHeatsByCompetition(competitionId)
            assertTrue(heats.isNotEmpty())
            assertTrue(heats[0].score == 3)
        }

    companion object {
        private fun randomString() = "String_${abs(Random.nextLong())}"

        private const val UID = 3
        private const val DATE = 1620000000000
        private const val ANOTHER_DATE = 1743801600000 // (02-08-2025)
        private const val MICRO_ID = 4
    }
}

package waveCoach.repository.jdbi

import waveCoach.domain.ActivityType
import waveCoach.domain.HeatToInsert
import kotlin.test.Test
import kotlin.test.assertTrue

class JdbiActivityRepositoryTest {
    @Test
    fun `get athlete activity list`() =
        testWithHandleAndRollback { handle ->
            val activityRepository = JdbiActivityRepository(handle)

            val activity = activityRepository.getAthleteActivityList(FIRST_ATHLETE_ID)

            assertTrue(activity.isNotEmpty())
        }

    @Test
    fun `store activity`() =
        testWithHandleAndRollback { handle ->
            val activityRepository = JdbiActivityRepository(handle)

            activityRepository.storeActivity(FIRST_ATHLETE_ID, DATE, MICRO_ID)

            val activityList = activityRepository.getAthleteActivityList(FIRST_ATHLETE_ID)

            assertTrue(activityList.isNotEmpty())
        }

    @Test
    fun `get calendar`() =
        testWithHandleAndRollback { handle ->
            val activityRepository = JdbiActivityRepository(handle)

            assertTrue(activityRepository.getCalendar(FIRST_ATHLETE_ID, null).isNotEmpty())
            assertTrue(activityRepository.getCalendar(FIRST_ATHLETE_ID, ActivityType.GYM).isNotEmpty())
            assertTrue(activityRepository.getCalendar(FIRST_ATHLETE_ID, ActivityType.WATER).isNotEmpty())
        }

    @Test
    fun `get activity by id`() =
        testWithHandleAndRollback { handle ->
            val activityRepository = JdbiActivityRepository(handle)

            val activity = activityRepository.getActivityById(1)

            assertTrue(activity != null)
        }

    @Test
    fun `get activity by heat id`() =
        testWithHandleAndRollback { handle ->
            val activityRepository = JdbiActivityRepository(handle)
            val waterActivityRepository = JdbiWaterActivityRepository(handle)
            val competitionRepository = JdbiCompetitionRepository(handle)

            val competition = competitionRepository.storeCompetition(FIRST_ATHLETE_ID, DATE, "Test Location", 1)

            val activity = activityRepository.storeActivity(FIRST_ATHLETE_ID, DATE, MICRO_ID)
            waterActivityRepository.storeWaterActivity(activity, 1, "good", 5, 10,)

            val heatId = competitionRepository.storeHeats(
                listOf(
                    HeatToInsert(
                        competitionId = competition,
                        waterActivityId = activity,
                        score = 10
                    )
                )
            )

            val heatActivity = activityRepository.getActivityByHeatId(heatId[0])

            assertTrue(heatActivity != null)
            assertTrue { heatActivity.id == activity }
        }

    @Test
    fun `remove activities`() =
        testWithHandleAndRollback { handle ->
            val activityRepository = JdbiActivityRepository(handle)
            val gymActivityRepository = JdbiGymActivityRepository(handle)

            gymActivityRepository.removeGymActivities(SECOND_ATHLETE_ID)

            activityRepository.storeActivity(SECOND_ATHLETE_ID, DATE, MICRO_ID)

            activityRepository.removeActivities(SECOND_ATHLETE_ID)

            val activityList = activityRepository.getAthleteActivityList(SECOND_ATHLETE_ID)

            assertTrue(activityList.isEmpty())
        }

    @Test
    fun `remove activity`() =
        testWithHandleAndRollback { handle ->
            val activityRepository = JdbiActivityRepository(handle)
            val gymActivityRepository = JdbiGymActivityRepository(handle)

            gymActivityRepository.removeGymActivity(ACTIVITY_ID)

            activityRepository.removeActivity(ACTIVITY_ID)

            val activityList = activityRepository.getAthleteActivityList(SECOND_ATHLETE_ID)

            assertTrue(activityList.isEmpty())
        }

    companion object {
        private const val DATE = 1743801600000 // (02-08-2025)
        private const val FIRST_ATHLETE_ID = 3
        private const val SECOND_ATHLETE_ID = 4
        private const val ACTIVITY_ID = 4
        private const val MICRO_ID = 4
    }
}

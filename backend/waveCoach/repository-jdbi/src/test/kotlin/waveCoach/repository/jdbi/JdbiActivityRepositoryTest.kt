package waveCoach.repository.jdbi

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

            activityRepository.storeActivity(FIRST_ATHLETE_ID, DATE, TYPE)

            val activityList = activityRepository.getAthleteActivityList(FIRST_ATHLETE_ID)

            assertTrue(activityList.isNotEmpty() && activityList.size == 2)
        }

    @Test
    fun `get activity by id`() =
        testWithHandleAndRollback { handle ->
            val activityRepository = JdbiActivityRepository(handle)

            val activity = activityRepository.getActivityById(1)

            assertTrue(activity != null)
        }

    @Test
    fun `remove activities`() =
        testWithHandleAndRollback { handle ->
            val activityRepository = JdbiActivityRepository(handle)
            val gymActivityRepository = JdbiGymActivityRepository(handle)

            gymActivityRepository.removeGymActivities(SECOND_ATHLETE_ID)

            activityRepository.storeActivity(SECOND_ATHLETE_ID, DATE, TYPE)

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
        private const val DATE = 948758400000 // (15-05-2000)
        private const val FIRST_ATHLETE_ID = 3
        private const val SECOND_ATHLETE_ID = 4
        private const val ACTIVITY_ID = 4
        private const val TYPE = "gym"
    }
}

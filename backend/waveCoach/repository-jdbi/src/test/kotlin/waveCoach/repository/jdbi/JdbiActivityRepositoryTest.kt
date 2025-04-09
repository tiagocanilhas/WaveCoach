package waveCoach.repository.jdbi

import kotlin.test.Test
import kotlin.test.assertTrue

class JdbiActivityRepositoryTest {

    @Test
    fun `get athlete activity list`() = testWithHandleAndRollback { handle ->
        val activityRepository = JdbiActivityRepository(handle)

        val activity = activityRepository.getAthleteActivityList(FIRST_ATHLETE_ID)

        assertTrue(activity.isNotEmpty())
    }

    @Test
    fun `store activity`() = testWithHandleAndRollback { handle ->
        val activityRepository = JdbiActivityRepository(handle)

        activityRepository.storeActivity(FIRST_ATHLETE_ID, DATE)

        val activityList = activityRepository.getAthleteActivityList(FIRST_ATHLETE_ID)

        assertTrue(activityList.isNotEmpty() && activityList.size == 2)
    }


    @Test
    fun `remove activities`() = testWithHandleAndRollback { handle ->
        val activityRepository = JdbiActivityRepository(handle)

        activityRepository.storeActivity(SECOND_ATHLETE_ID, DATE)

        activityRepository.removeActivities(SECOND_ATHLETE_ID)

        val activityList = activityRepository.getAthleteActivityList(SECOND_ATHLETE_ID)

        assertTrue(activityList.isEmpty())
    }

    companion object {
        private const val DATE = 948758400000 // (15-05-2000)
        private const val FIRST_ATHLETE_ID = 3
        private const val SECOND_ATHLETE_ID = 4
    }
}
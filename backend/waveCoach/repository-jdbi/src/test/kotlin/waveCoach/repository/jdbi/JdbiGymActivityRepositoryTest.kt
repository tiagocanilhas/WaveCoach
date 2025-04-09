package waveCoach.repository.jdbi

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class JdbiGymActivityRepositoryTest {

    @Test
    fun `store Gym activity`() = testWithHandleAndRollback { handle ->
        val gymActivityRepository = JdbiGymActivityRepository(handle)
        val activityRepository = JdbiActivityRepository(handle)

        val activityId = activityRepository.storeActivity(FIRST_ATHLETE_ID, DATE)

        val gymActivityId = gymActivityRepository.storeGymActivity(activityId)

        assertEquals(activityId, gymActivityId)
    }

    @Test
    fun `get Gym activities`() = testWithHandleAndRollback { handle ->
        val gymActivityRepository = JdbiGymActivityRepository(handle)

        val gymActivityList = gymActivityRepository.getGymActivities(FIRST_ATHLETE_ID)

        assertTrue { gymActivityList.isNotEmpty() }
    }

    @Test
    fun `remove Gym activities`() = testWithHandleAndRollback { handle ->
        val gymActivityRepository = JdbiGymActivityRepository(handle)

        gymActivityRepository.removeGymActivities(FIRST_ATHLETE_ID)

        val gymActivityList = gymActivityRepository.getGymActivities(FIRST_ATHLETE_ID)

        assertTrue { gymActivityList.isEmpty() }
    }

    companion object {
        private const val DATE = 948758400000 // (15-05-2000)
        private const val FIRST_ATHLETE_ID = 3
    }
}
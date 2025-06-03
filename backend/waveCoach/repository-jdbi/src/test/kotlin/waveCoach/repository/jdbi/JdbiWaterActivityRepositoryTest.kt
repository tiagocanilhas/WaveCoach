package waveCoach.repository.jdbi

import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.Test

class JdbiWaterActivityRepositoryTest {
    @Test
    fun `store water activity`() =
        testWithHandleAndRollback { handle ->
            val waterActivityRepository = JdbiWaterActivityRepository(handle)

            val activityId =
                waterActivityRepository.storeWaterActivity(
                    WATER_ACTIVITY_ID_TO_BE_CREATED,
                    FIRST_RPE,
                    FIRST_CONDITION,
                    FIRST_TRIMP,
                    FIRST_DURATION,
                )

            assertTrue(activityId > 0)
        }

    @Test
    fun `get water activity`() =
        testWithHandleAndRollback { handle ->
            val waterActivityRepository = JdbiWaterActivityRepository(handle)

            val activityId =
                waterActivityRepository.storeWaterActivity(
                    FIRST_ACTIVITY_ID,
                    FIRST_RPE,
                    FIRST_CONDITION,
                    FIRST_TRIMP,
                    FIRST_DURATION,
                )

            val waterActivity = waterActivityRepository.getWaterActivity(activityId)

            assertTrue(waterActivity != null)
            assertTrue(waterActivity!!.id == activityId)
            assertTrue(waterActivity.rpe == FIRST_RPE)
            assertTrue(waterActivity.condition == FIRST_CONDITION)
            assertTrue(waterActivity.trimp == FIRST_TRIMP)
        }

    @Test
    fun `remove water activity`() =
        testWithHandleAndRollback { handle ->
            val waterActivityRepository = JdbiWaterActivityRepository(handle)

            waterActivityRepository.removeWaterActivity(SECOND_WATER_ACTIVITY_ID)

            val waterActivity = waterActivityRepository.getWaterActivity(SECOND_WATER_ACTIVITY_ID)
            assertTrue(waterActivity == null)
        }

    @Test
    fun `store wave`() =
        testWithHandleAndRollback { handle ->
            val waterActivityRepository = JdbiWaterActivityRepository(handle)

            val activityId =
                waterActivityRepository.storeWaterActivity(
                    FIRST_ACTIVITY_ID,
                    FIRST_RPE,
                    FIRST_CONDITION,
                    FIRST_TRIMP,
                    FIRST_DURATION,
                )

            val waveId =
                waterActivityRepository.storeWave(
                    activityId,
                    null,
                    true,
                    1,
                )

            assertTrue(waveId > 0)
        }

    @Test
    fun `store maneuver`() =
        testWithHandleAndRollback { handle ->
            val waterActivityRepository = JdbiWaterActivityRepository(handle)

            val activityId =
                waterActivityRepository.storeWaterActivity(
                    FIRST_ACTIVITY_ID,
                    FIRST_RPE,
                    FIRST_CONDITION,
                    FIRST_TRIMP,
                    FIRST_DURATION,
                )

            val waveId =
                waterActivityRepository.storeWave(
                    activityId,
                    null,
                    true,
                    1,
                )

            val maneuverId =
                waterActivityRepository.storeManeuver(
                    waveId,
                    1,
                    true,
                    1,
                )

            assertTrue(maneuverId > 0)
        }


    companion object {
        const val FIRST_ACTIVITY_ID = 1

        const val WATER_ACTIVITY_ID_TO_BE_CREATED = 4
        const val SECOND_WATER_ACTIVITY_ID = 3
        const val FIRST_RPE = 5
        const val FIRST_CONDITION = "Good"
        const val FIRST_TRIMP = 120
        const val FIRST_DURATION = 30
    }
}

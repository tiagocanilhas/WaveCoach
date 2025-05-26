package waveCoach.repository.jdbi

import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.Test

class JdbiWaterActivityRepositoryTest {
    @Test
    fun `store water activity`() =
        testWithHandleAndRollback { handle ->
            val waterActivityRepository = JdbiWaterActivityRepository(handle)

            val activityId = waterActivityRepository.storeWaterActivity(
                FIRST_WATER_ACTIVITY_ID,
                FIRST_PSE,
                FIRST_CONDITION,
                FIRST_HEART_RATE,
                FIRST_DURATION
            )

            assertTrue(activityId > 0)
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

            val activityId = waterActivityRepository.storeWaterActivity(
                FIRST_ACTIVITY_ID,
                FIRST_PSE,
                FIRST_CONDITION,
                FIRST_HEART_RATE,
                FIRST_DURATION
            )

            val waveId = waterActivityRepository.storeWave(
                activityId,
                null,
                1
            )

            assertTrue(waveId > 0)
        }

    @Test
    fun `store maneuver`() =
        testWithHandleAndRollback { handle ->
            val waterActivityRepository = JdbiWaterActivityRepository(handle)

            val activityId = waterActivityRepository.storeWaterActivity(
                FIRST_ACTIVITY_ID,
                FIRST_PSE,
                FIRST_CONDITION,
                FIRST_HEART_RATE,
                FIRST_DURATION
            )

            val waveId = waterActivityRepository.storeWave(
                activityId,
                null,
                1
            )

            val maneuverId = waterActivityRepository.storeManeuver(
                waveId,
                1,
                rightSide = true,
                true,
                1
            )

            assertTrue(maneuverId > 0)
        }

    @Test
    fun `get water activity`() =
        testWithHandleAndRollback { handle ->
            val waterActivityRepository = JdbiWaterActivityRepository(handle)

            val activityId = waterActivityRepository.storeWaterActivity(
                FIRST_ACTIVITY_ID,
                FIRST_PSE,
                FIRST_CONDITION,
                FIRST_HEART_RATE,
                FIRST_DURATION
            )

            val waterActivity = waterActivityRepository.getWaterActivity(activityId)

            assertTrue(waterActivity != null)
            assertTrue(waterActivity!!.id == activityId)
            assertTrue(waterActivity.pse == FIRST_PSE)
            assertTrue(waterActivity.condition == FIRST_CONDITION)
            assertTrue(waterActivity.heartRate == FIRST_HEART_RATE)
        }



    companion object{
        const val FIRST_ACTIVITY_ID = 1

        const val FIRST_WATER_ACTIVITY_ID = 2
        const val SECOND_WATER_ACTIVITY_ID = 3
        const val FIRST_PSE = 5
        const val FIRST_CONDITION = "Good"
        const val FIRST_HEART_RATE = 120
        const val FIRST_DURATION = 30
    }
}
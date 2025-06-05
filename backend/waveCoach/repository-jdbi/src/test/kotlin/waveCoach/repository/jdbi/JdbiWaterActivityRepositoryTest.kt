package waveCoach.repository.jdbi

import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.Test

class JdbiWaterActivityRepositoryTest {
    @Test
    fun `store water activity`() =
        testWithHandleAndRollback { handle ->
            val waterActivityRepository = JdbiWaterActivityRepository(handle)
            val activityRepository = JdbiActivityRepository(handle)

            val activityId = activityRepository.storeActivity(
                ATHLETE_ID,
                DATE_LONG,
                MICROCYCLE_ID
            )

            val waterActivityId = waterActivityRepository.storeWaterActivity(
                activityId,
                FIRST_RPE,
                FIRST_CONDITION,
                FIRST_TRIMP,
                FIRST_DURATION
            )

            assertTrue(waterActivityId == activityId)
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

            val activity = waterActivityRepository.isWaterActivityValid(SECOND_WATER_ACTIVITY_ID)
            assertTrue(!activity)
        }

    @Test
    fun `is water activity valid`() =
        testWithHandleAndRollback { handle ->
            val waterActivityRepository = JdbiWaterActivityRepository(handle)

            val isValid = waterActivityRepository.isWaterActivityValid(SECOND_WATER_ACTIVITY_ID)

            assertTrue(isValid)
        }

    @Test
    fun `store wave`() =
        testWithHandleAndRollback { handle ->
            val waterActivityRepository = JdbiWaterActivityRepository(handle)
            val activityRepository = JdbiActivityRepository(handle)

            val activityId = activityRepository.storeActivity(
                ATHLETE_ID,
                DATE_LONG,
                MICROCYCLE_ID
            )

            val waterActivityId = waterActivityRepository.storeWaterActivity(
                activityId,
                FIRST_RPE,
                FIRST_CONDITION,
                FIRST_TRIMP,
                FIRST_DURATION
            )

            val waveId = waterActivityRepository.storeWave(
                waterActivityId,
                null,
                true,
                1
            )

            assertTrue(waveId > 0)
        }

    @Test
    fun `store maneuver`() =
        testWithHandleAndRollback { handle ->
            val waterActivityRepository = JdbiWaterActivityRepository(handle)
            val activityRepository = JdbiActivityRepository(handle)

            val activityId = activityRepository.storeActivity(
                ATHLETE_ID,
                DATE_LONG,
                MICROCYCLE_ID
            )

            val waterActivityId = waterActivityRepository.storeWaterActivity(
                activityId,
                FIRST_RPE,
                FIRST_CONDITION,
                FIRST_TRIMP,
                FIRST_DURATION
            )

            val waveId = waterActivityRepository.storeWave(
                waterActivityId,
                null,
                true,
                1
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

    @Test
    fun `remove waves`() =
        testWithHandleAndRollback { handle ->
            val waterActivityRepository = JdbiWaterActivityRepository(handle)
            val activityRepository = JdbiActivityRepository(handle)

            val activityId = activityRepository.storeActivity(
                ATHLETE_ID,
                DATE_LONG,
                MICROCYCLE_ID
            )

            val waterActivityId = waterActivityRepository.storeWaterActivity(
                activityId,
                FIRST_RPE,
                FIRST_CONDITION,
                FIRST_TRIMP,
                FIRST_DURATION
            )

            waterActivityRepository.storeWave(
                waterActivityId,
                null,
                true,
                1
            )

            waterActivityRepository.removeWavesByActivity(activityId)

            val activity = waterActivityRepository.getWaterActivity(waterActivityId)
            assertTrue(activity!!.waves.isEmpty())
        }

    @Test
    fun `remove maneuver`() =
        testWithHandleAndRollback { handle ->
            val waterActivityRepository = JdbiWaterActivityRepository(handle)
            val activityRepository = JdbiActivityRepository(handle)

            val activityId = activityRepository.storeActivity(
                ATHLETE_ID,
                DATE_LONG,
                MICROCYCLE_ID
            )

            val waterActivityId = waterActivityRepository.storeWaterActivity(
                activityId,
                FIRST_RPE,
                FIRST_CONDITION,
                FIRST_TRIMP,
                FIRST_DURATION
            )

            val waveId = waterActivityRepository.storeWave(
                waterActivityId,
                null,
                true,
                1
            )

            waterActivityRepository.storeManeuver(
                waveId,
                1,
                true,
                1
            )

            waterActivityRepository.removeManeuversByActivity(waterActivityId)

            val activity = waterActivityRepository.getWaterActivity(waterActivityId)
            assertTrue(activity!!.waves.all { it.maneuvers.isEmpty() })
        }







    companion object{
        const val FIRST_ACTIVITY_ID = 1
        const val DATE_LONG = 1633036800000L
        const val MICROCYCLE_ID = 1
        const val ATHLETE_ID = 3

        const val SECOND_WATER_ACTIVITY_ID = 3
        const val FIRST_RPE = 5
        const val FIRST_CONDITION = "Good"
        const val FIRST_TRIMP = 120
        const val FIRST_DURATION = 30
    }
}

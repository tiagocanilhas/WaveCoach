package waveCoach.repository.jdbi

import org.junit.jupiter.api.Assertions.assertTrue
import waveCoach.domain.ManeuverToInsert
import waveCoach.domain.ManeuverToUpdate
import waveCoach.domain.WaveToUpdate
import kotlin.test.Test
import kotlin.test.assertFalse

class JdbiWaterActivityRepositoryTest {
    @Test
    fun `store water activity`() =
        testWithHandleAndRollback { handle ->
            val waterActivityRepository = JdbiWaterActivityRepository(handle)
            val activityRepository = JdbiActivityRepository(handle)

            val activityId =
                activityRepository.storeActivity(
                    ATHLETE_ID,
                    DATE_LONG,
                    MICROCYCLE_ID,
                )

            val waterActivityId =
                waterActivityRepository.storeWaterActivity(
                    activityId,
                    FIRST_RPE,
                    FIRST_CONDITION,
                    FIRST_TRIMP,
                    FIRST_DURATION,
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
    fun `update water activity`() =
        testWithHandleAndRollback { handle ->
            val waterActivityRepository = JdbiWaterActivityRepository(handle)

            waterActivityRepository.updateWaterActivity(
                FIRST_WATER_ACTIVITY_ID,
                FIRST_RPE + 1,
                "Excellent",
                FIRST_TRIMP + 10,
                FIRST_DURATION + 5,
            )

            val updatedActivity = waterActivityRepository.getWaterActivity(FIRST_WATER_ACTIVITY_ID)

            assertTrue(updatedActivity != null)
            assertTrue(updatedActivity!!.rpe == FIRST_RPE + 1)
            assertTrue(updatedActivity.condition == "Excellent")
            assertTrue(updatedActivity.trimp == FIRST_TRIMP + 10)
            assertTrue(updatedActivity.duration == FIRST_DURATION + 5)
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

            val activityId =
                activityRepository.storeActivity(
                    ATHLETE_ID,
                    DATE_LONG,
                    MICROCYCLE_ID,
                )

            val waterActivityId =
                waterActivityRepository.storeWaterActivity(
                    activityId,
                    FIRST_RPE,
                    FIRST_CONDITION,
                    FIRST_TRIMP,
                    FIRST_DURATION,
                )

            val waveId =
                waterActivityRepository.storeWave(
                    waterActivityId,
                    null,
                    true,
                    1,
                )

            assertTrue(waveId > 0)
        }

    @Test
    fun `get wave by id`() =
        testWithHandleAndRollback { handle ->
            val waterActivityRepository = JdbiWaterActivityRepository(handle)
            val activityRepository = JdbiActivityRepository(handle)

            val activityId =
                activityRepository.storeActivity(
                    ATHLETE_ID,
                    DATE_LONG,
                    MICROCYCLE_ID,
                )

            val waterActivityId =
                waterActivityRepository.storeWaterActivity(
                    activityId,
                    FIRST_RPE,
                    FIRST_CONDITION,
                    FIRST_TRIMP,
                    FIRST_DURATION,
                )

            val waveId =
                waterActivityRepository.storeWave(
                    waterActivityId,
                    null,
                    true,
                    1,
                )

            val wave = waterActivityRepository.getWaveById(waveId)

            assertTrue(wave != null)
            assertTrue(wave!!.id == waveId)
        }

    @Test
    fun `get wave by activity`() =
        testWithHandleAndRollback { handle ->
            val waterActivityRepository = JdbiWaterActivityRepository(handle)
            val activityRepository = JdbiActivityRepository(handle)

            val activityId =
                activityRepository.storeActivity(
                    ATHLETE_ID,
                    DATE_LONG,
                    MICROCYCLE_ID,
                )

            val waterActivityId =
                waterActivityRepository.storeWaterActivity(
                    activityId,
                    FIRST_RPE,
                    FIRST_CONDITION,
                    FIRST_TRIMP,
                    FIRST_DURATION,
                )

            val waveID =
                waterActivityRepository.storeWave(
                    waterActivityId,
                    null,
                    true,
                    1,
                )

            val waves = waterActivityRepository.getWavesByActivity(waterActivityId)

            assertTrue(waves.isNotEmpty())
            assertTrue(waves.any { it.id == waveID })
        }

    @Test
    fun `update waves`() =
        testWithHandleAndRollback { handle ->
            val waterActivityRepository = JdbiWaterActivityRepository(handle)
            val activityRepository = JdbiActivityRepository(handle)

            val activityId =
                activityRepository.storeActivity(
                    ATHLETE_ID,
                    DATE_LONG,
                    MICROCYCLE_ID,
                )

            val waterActivityId =
                waterActivityRepository.storeWaterActivity(
                    activityId,
                    FIRST_RPE,
                    FIRST_CONDITION,
                    FIRST_TRIMP,
                    FIRST_DURATION,
                )

            val waveId =
                waterActivityRepository.storeWave(
                    waterActivityId,
                    null,
                    true,
                    1,
                )

            val updatedWaves = listOf(WaveToUpdate(waveId, 10f, false, null))
            waterActivityRepository.updateWaves(updatedWaves)

            val wave = waterActivityRepository.getWaveById(waveId)
            assertTrue(wave != null)
            assertTrue(wave!!.points == 10f)
            assertTrue(!wave.rightSide)
            assertTrue(wave.waveOrder == 1)
        }

    @Test
    fun `store maneuver`() =
        testWithHandleAndRollback { handle ->
            val waterActivityRepository = JdbiWaterActivityRepository(handle)
            val activityRepository = JdbiActivityRepository(handle)

            val activityId =
                activityRepository.storeActivity(
                    ATHLETE_ID,
                    DATE_LONG,
                    MICROCYCLE_ID,
                )

            val waterActivityId =
                waterActivityRepository.storeWaterActivity(
                    activityId,
                    FIRST_RPE,
                    FIRST_CONDITION,
                    FIRST_TRIMP,
                    FIRST_DURATION,
                )

            val waveId =
                waterActivityRepository.storeWave(
                    waterActivityId,
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

    @Test
    fun `store maneuvers`() =
        testWithHandleAndRollback { handle ->
            val waterActivityRepository = JdbiWaterActivityRepository(handle)
            val activityRepository = JdbiActivityRepository(handle)

            val activityId =
                activityRepository.storeActivity(
                    ATHLETE_ID,
                    DATE_LONG,
                    MICROCYCLE_ID,
                )

            val waterActivityId =
                waterActivityRepository.storeWaterActivity(
                    activityId,
                    FIRST_RPE,
                    FIRST_CONDITION,
                    FIRST_TRIMP,
                    FIRST_DURATION,
                )

            val waveId =
                waterActivityRepository.storeWave(
                    waterActivityId,
                    null,
                    true,
                    1,
                )

            val maneuvers =
                listOf(
                    ManeuverToInsert(waveId, 1, true, 1),
                    ManeuverToInsert(waveId, 2, false, 2),
                )

            val maneuverIds = waterActivityRepository.storeManeuvers(maneuvers)

            assertTrue(maneuverIds.size == 2)
        }

    @Test
    fun `get maneuver by id`() =
        testWithHandleAndRollback { handle ->
            val waterActivityRepository = JdbiWaterActivityRepository(handle)
            val activityRepository = JdbiActivityRepository(handle)

            val activityId =
                activityRepository.storeActivity(
                    ATHLETE_ID,
                    DATE_LONG,
                    MICROCYCLE_ID,
                )

            val waterActivityId =
                waterActivityRepository.storeWaterActivity(
                    activityId,
                    FIRST_RPE,
                    FIRST_CONDITION,
                    FIRST_TRIMP,
                    FIRST_DURATION,
                )

            val waveId =
                waterActivityRepository.storeWave(
                    waterActivityId,
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

            val maneuver = waterActivityRepository.getManeuverById(maneuverId)

            assertTrue(maneuver != null)
            assertTrue(maneuver!!.id == maneuverId)
            assertTrue(maneuver.wave == waveId)
            assertTrue(maneuver.waterManeuverId == 1)
            assertTrue(maneuver.success)
            assertTrue(maneuver.maneuverOrder == 1)
        }

    @Test
    fun `get maneuvers by wave`() =
        testWithHandleAndRollback { handle ->
            val waterActivityRepository = JdbiWaterActivityRepository(handle)
            val activityRepository = JdbiActivityRepository(handle)

            val activityId =
                activityRepository.storeActivity(
                    ATHLETE_ID,
                    DATE_LONG,
                    MICROCYCLE_ID,
                )

            val waterActivityId =
                waterActivityRepository.storeWaterActivity(
                    activityId,
                    FIRST_RPE,
                    FIRST_CONDITION,
                    FIRST_TRIMP,
                    FIRST_DURATION,
                )

            val waveId =
                waterActivityRepository.storeWave(
                    waterActivityId,
                    null,
                    true,
                    1,
                )

            val waveId1 =
                waterActivityRepository.storeManeuver(
                    waveId,
                    1,
                    true,
                    1,
                )
            val waveId2 =
                waterActivityRepository.storeManeuver(
                    waveId,
                    2,
                    false,
                    2,
                )

            val maneuvers = waterActivityRepository.getManeuversByWave(waveId)

            assertTrue(maneuvers.size == 2)
            assertTrue(maneuvers.any { it.id == waveId1 })
            assertTrue(maneuvers.any { it.id == waveId2 })
        }

    @Test
    fun `update maneuvers`() =
        testWithHandleAndRollback { handle ->
            val waterActivityRepository = JdbiWaterActivityRepository(handle)
            val activityRepository = JdbiActivityRepository(handle)

            val activityId =
                activityRepository.storeActivity(
                    ATHLETE_ID,
                    DATE_LONG,
                    MICROCYCLE_ID,
                )

            val waterActivityId =
                waterActivityRepository.storeWaterActivity(
                    activityId,
                    FIRST_RPE,
                    FIRST_CONDITION,
                    FIRST_TRIMP,
                    FIRST_DURATION,
                )

            val waveId =
                waterActivityRepository.storeWave(
                    waterActivityId,
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

            val updatedManeuvers = listOf(ManeuverToUpdate(maneuverId, 1, false, null))
            waterActivityRepository.updateManeuvers(updatedManeuvers)

            val maneuver = waterActivityRepository.getManeuverById(maneuverId)
            assertTrue(maneuver != null)
            assertTrue(!maneuver!!.success)
            assertTrue(maneuver.maneuverOrder == 1)
        }

    @Test
    fun `remove waves`() =
        testWithHandleAndRollback { handle ->
            val waterActivityRepository = JdbiWaterActivityRepository(handle)
            val activityRepository = JdbiActivityRepository(handle)

            val activityId =
                activityRepository.storeActivity(
                    ATHLETE_ID,
                    DATE_LONG,
                    MICROCYCLE_ID,
                )

            val waterActivityId =
                waterActivityRepository.storeWaterActivity(
                    activityId,
                    FIRST_RPE,
                    FIRST_CONDITION,
                    FIRST_TRIMP,
                    FIRST_DURATION,
                )

            waterActivityRepository.storeWave(
                waterActivityId,
                null,
                true,
                1,
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

            val activityId =
                activityRepository.storeActivity(
                    ATHLETE_ID,
                    DATE_LONG,
                    MICROCYCLE_ID,
                )

            val waterActivityId =
                waterActivityRepository.storeWaterActivity(
                    activityId,
                    FIRST_RPE,
                    FIRST_CONDITION,
                    FIRST_TRIMP,
                    FIRST_DURATION,
                )

            val waveId =
                waterActivityRepository.storeWave(
                    waterActivityId,
                    null,
                    true,
                    1,
                )

            waterActivityRepository.storeManeuver(
                waveId,
                1,
                true,
                1,
            )

            waterActivityRepository.removeManeuversByActivity(waterActivityId)

            val activity = waterActivityRepository.getWaterActivity(waterActivityId)
            assertTrue(activity!!.waves.all { it.maneuvers.isEmpty() })
        }

    @Test
    fun `remove maneuver by id`() =
        testWithHandleAndRollback { handle ->
            val waterActivityRepository = JdbiWaterActivityRepository(handle)
            val activityRepository = JdbiActivityRepository(handle)

            val activityId =
                activityRepository.storeActivity(
                    ATHLETE_ID,
                    DATE_LONG,
                    MICROCYCLE_ID,
                )

            val waterActivityId =
                waterActivityRepository.storeWaterActivity(
                    activityId,
                    FIRST_RPE,
                    FIRST_CONDITION,
                    FIRST_TRIMP,
                    FIRST_DURATION,
                )

            val waveId =
                waterActivityRepository.storeWave(
                    waterActivityId,
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

            waterActivityRepository.removeManeuverById(maneuverId)

            val result = waterActivityRepository.getManeuverById(maneuverId)
            assertTrue(result == null)
        }

    @Test
    fun `remove maneuvers by ids`() =
        testWithHandleAndRollback { handle ->
            val waterActivityRepository = JdbiWaterActivityRepository(handle)
            val activityRepository = JdbiActivityRepository(handle)

            val activityId =
                activityRepository.storeActivity(
                    ATHLETE_ID,
                    DATE_LONG,
                    MICROCYCLE_ID,
                )

            val waterActivityId =
                waterActivityRepository.storeWaterActivity(
                    activityId,
                    FIRST_RPE,
                    FIRST_CONDITION,
                    FIRST_TRIMP,
                    FIRST_DURATION,
                )

            val waveId =
                waterActivityRepository.storeWave(
                    waterActivityId,
                    null,
                    true,
                    1,
                )

            val maneuverIds =
                listOf(
                    waterActivityRepository.storeManeuver(waveId, 1, true, 1),
                    waterActivityRepository.storeManeuver(waveId, 2, false, 2),
                )

            waterActivityRepository.removeManeuversById(maneuverIds)

            maneuverIds.forEach { id ->
                val result = waterActivityRepository.getManeuverById(id)
                assertTrue(result == null)
            }
        }

    @Test
    fun `remove wave by id`() =
        testWithHandleAndRollback { handle ->
            val waterActivityRepository = JdbiWaterActivityRepository(handle)
            val activityRepository = JdbiActivityRepository(handle)

            val activityId =
                activityRepository.storeActivity(
                    ATHLETE_ID,
                    DATE_LONG,
                    MICROCYCLE_ID,
                )

            val waterActivityId =
                waterActivityRepository.storeWaterActivity(
                    activityId,
                    FIRST_RPE,
                    FIRST_CONDITION,
                    FIRST_TRIMP,
                    FIRST_DURATION,
                )

            val waveId =
                waterActivityRepository.storeWave(
                    waterActivityId,
                    null,
                    true,
                    1,
                )

            waterActivityRepository.removeWaveById(waveId)

            val result = waterActivityRepository.getWaveById(waveId)
            assertTrue(result == null)
        }

    @Test
    fun `remove waves by ids`() =
        testWithHandleAndRollback { handle ->
            val waterActivityRepository = JdbiWaterActivityRepository(handle)
            val activityRepository = JdbiActivityRepository(handle)

            val activityId =
                activityRepository.storeActivity(
                    ATHLETE_ID,
                    DATE_LONG,
                    MICROCYCLE_ID,
                )

            val waterActivityId =
                waterActivityRepository.storeWaterActivity(
                    activityId,
                    FIRST_RPE,
                    FIRST_CONDITION,
                    FIRST_TRIMP,
                    FIRST_DURATION,
                )

            val waveIds =
                listOf(
                    waterActivityRepository.storeWave(waterActivityId, null, true, 1),
                    waterActivityRepository.storeWave(waterActivityId, null, false, 2),
                )

            waterActivityRepository.removeWavesById(waveIds)

            waveIds.forEach { id ->
                val result = waterActivityRepository.getWaveById(id)
                assertTrue(result == null)
            }
        }

    @Test
    fun `verify wave order`() =
        testWithHandleAndRollback { handle ->
            val waterActivityRepository = JdbiWaterActivityRepository(handle)

            val result1 = waterActivityRepository.verifyWaveOrder(FIRST_WATER_ACTIVITY_ID, 1)
            val result2 = waterActivityRepository.verifyWaveOrder(FIRST_WATER_ACTIVITY_ID, 3)

            assertTrue(result1)
            assertFalse(result2)
        }

    @Test
    fun `verify maneuver order`() =
        testWithHandleAndRollback { handle ->
            val waterActivityRepository = JdbiWaterActivityRepository(handle)

            val result1 = waterActivityRepository.verifyManeuverOrder(FIRST_WAVE_ID, 1)
            val result2 = waterActivityRepository.verifyManeuverOrder(FIRST_WAVE_ID, 3)

            assertTrue(result1)
            assertFalse(result2)
        }

    companion object {
        const val FIRST_ACTIVITY_ID = 1
        const val FIRST_WATER_ACTIVITY_ID = 2
        const val FIRST_WAVE_ID = 1
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

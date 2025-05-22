package waveCoach.repository.jdbi

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class JdbiWaterManeuverRepositoryTest {

    /**
     * Store and get Water Maneuver Test
     */

    @Test
    fun `store  and get water maneuver`() =
        testWithHandleAndRollback { handle ->
            val waterManeuversRepository = JdbiWaterManeuverRepository(handle)

            val name = "Water Maneuver"

            waterManeuversRepository.storeWaterManeuver(name)
            val maneuver = waterManeuversRepository.getWaterManeuverByName(name)!!

            assertEquals(maneuver.name, name)
        }

    @Test
    fun `valid water maneuver`() =
        testWithHandleAndRollback { handle ->
            val waterManeuversRepository = JdbiWaterManeuverRepository(handle)

            val res = waterManeuversRepository.isWaterManeuverValid(1)

            assertEquals(res, true)

            val res2 = waterManeuversRepository.isWaterManeuverValid(0)

            assertEquals(res2, false)
        }
}
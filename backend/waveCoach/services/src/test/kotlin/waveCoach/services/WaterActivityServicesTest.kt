package waveCoach.services

import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.Assertions.assertTrue
import org.postgresql.ds.PGSimpleDataSource
import waveCoach.domain.WaterActivityDomain
import waveCoach.repository.jdbi.JdbiTransactionManager
import waveCoach.repository.jdbi.configureWithAppRequirements
import waveCoach.utils.Failure
import waveCoach.utils.Success
import kotlin.test.Test
import kotlin.test.fail

class WaterActivityServicesTest {
    /**
     * Create WaterActivity Test
     */

    @Test
    fun `create water activity - success`() {
        val createWaterActivityService = createWaterActivityServices()

        when (
            val result =
                createWaterActivityService.createWaterActivity(
                    FIRST_COACH_ID,
                    FIRST_ATHLETE_ID,
                    DATE,
                    PSE,
                    CONDITION,
                    HEART_RATE,
                    DURATION,
                    MANEUVERS_LIST,
                )
        ) {
            is Failure -> fail("Unexpected $result")
            is Success -> assertTrue(result.value > 0)
        }
    }

    @Test
    fun `create water activity - invalid date`() {
        val createWaterActivityService = createWaterActivityServices()

        val invalidDates =
            listOf(
                "32-01-2000",
                "2000-01-01",
            )

        invalidDates.forEach { date ->
            when (
                val result =
                    createWaterActivityService.createWaterActivity(
                        FIRST_COACH_ID,
                        FIRST_ATHLETE_ID,
                        date,
                        PSE,
                        CONDITION,
                        HEART_RATE,
                        DURATION,
                        MANEUVERS_LIST,
                    )
            ) {
                is Failure -> assertTrue(result.value is CreateWaterActivityError.InvalidDate)
                is Success -> fail("Unexpected $result")
            }
        }
    }

    @Test
    fun `create water activity - athlete not found`() {
        val createWaterActivityService = createWaterActivityServices()

        when (
            val result =
                createWaterActivityService.createWaterActivity(
                    FIRST_COACH_ID,
                    0,
                    DATE,
                    PSE,
                    CONDITION,
                    HEART_RATE,
                    DURATION,
                    MANEUVERS_LIST,
                )
        ) {
            is Failure -> assertTrue(result.value is CreateWaterActivityError.AthleteNotFound)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `create water activity - not athletes coach`() {
        val createWaterActivityService = createWaterActivityServices()

        when (
            val result =
                createWaterActivityService.createWaterActivity(
                    SECOND_COACH_ID,
                    FIRST_ATHLETE_ID,
                    DATE,
                    PSE,
                    CONDITION,
                    HEART_RATE,
                    DURATION,
                    MANEUVERS_LIST,
                )
        ) {
            is Failure -> assertTrue(result.value is CreateWaterActivityError.NotAthletesCoach)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `create water activity - invalid pse`() {
        val createWaterActivityService = createWaterActivityServices()

        val invalidPseList = listOf(-1, 0, 11)

        invalidPseList.forEach { pse ->
            when (
                val result =
                    createWaterActivityService.createWaterActivity(
                        FIRST_COACH_ID,
                        FIRST_ATHLETE_ID,
                        DATE,
                        pse,
                        CONDITION,
                        HEART_RATE,
                        DURATION,
                        MANEUVERS_LIST,
                    )
            ) {
                is Failure -> assertTrue(result.value is CreateWaterActivityError.InvalidPse)
                is Success -> fail("Unexpected $result")
            }
        }
    }

    @Test
    fun `create water activity - invalid heart rate`() {
        val createWaterActivityService = createWaterActivityServices()

        val invalidHeartRateList = listOf(-1, 0, 201)

        invalidHeartRateList.forEach { heartRate ->
            when (
                val result =
                    createWaterActivityService.createWaterActivity(
                        FIRST_COACH_ID,
                        FIRST_ATHLETE_ID,
                        DATE,
                        PSE,
                        CONDITION,
                        heartRate,
                        DURATION,
                        MANEUVERS_LIST,
                    )
            ) {
                is Failure -> assertTrue(result.value is CreateWaterActivityError.InvalidHeartRate)
                is Success -> fail("Unexpected $result")
            }
        }
    }

    @Test
    fun `create water activity - invalid duration`() {
        val createWaterActivityService = createWaterActivityServices()

        val invalidDuration = -1

        when (
            val result =
                createWaterActivityService.createWaterActivity(
                    FIRST_COACH_ID,
                    FIRST_ATHLETE_ID,
                    DATE,
                    PSE,
                    CONDITION,
                    HEART_RATE,
                    invalidDuration,
                    MANEUVERS_LIST,
                )
        ) {
            is Failure -> assertTrue(result.value is CreateWaterActivityError.InvalidDuration)
            is Success -> fail("Unexpected $result")
        }

    }

    @Test
    fun `create water activity - invalid maneuver`() {
        val createWaterActivityService = createWaterActivityServices()

        val invalidManeuver = 0

        when (
            val result =
                createWaterActivityService.createWaterActivity(
                    FIRST_COACH_ID,
                    FIRST_ATHLETE_ID,
                    DATE,
                    PSE,
                    CONDITION,
                    HEART_RATE,
                    DURATION,
                    listOf(
                        WaveInputInfo(
                            points = 10f,
                            maneuvers = listOf(
                                ManeuverInputInfo(
                                    waterManeuverId = invalidManeuver,
                                    rightSide = true,
                                    success = true
                                )
                            )
                        )
                    ),
                )
        ) {
            is Failure -> assertTrue(result.value is CreateWaterActivityError.InvalidWaterManeuver)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `create water activity - activity without microcycle`() {
        val createWaterActivityService = createWaterActivityServices()

        when (
            val result =
                createWaterActivityService.createWaterActivity(
                    FIRST_COACH_ID,
                    FIRST_ATHLETE_ID,
                    DATE_WITHOUT_MICROCYCLE,
                    PSE,
                    CONDITION,
                    HEART_RATE,
                    DURATION,
                    MANEUVERS_LIST,
                )
        ) {
            is Failure -> assertTrue(result.value is CreateWaterActivityError.ActivityWithoutMicrocycle)
            is Success -> fail("Unexpected $result")
        }
    }

    /**
     * Get WaterActivity Test
     */

    @Test
    fun `get water activity - success`() {
        val createWaterActivityService = createWaterActivityServices()

        when (
            val result = createWaterActivityService.getWaterActivity(FIRST_COACH_ID, FIRST_WATER_ACTIVITY_ID)
        ) {
            is Failure -> fail("Unexpected $result")
            is Success -> assertTrue(result.value.id == FIRST_WATER_ACTIVITY_ID)
        }
    }

    @Test
    fun `get water activity - not found`() {
        val createWaterActivityService = createWaterActivityServices()

        when (
            val result = createWaterActivityService.getWaterActivity(FIRST_COACH_ID, 0)
        ) {
            is Failure -> assertTrue(result.value is GetWaterActivityError.ActivityNotFound)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `get water activity - not athletes coach`() {
        val createWaterActivityService = createWaterActivityServices()

        when (
            val result = createWaterActivityService.getWaterActivity(SECOND_COACH_ID, FIRST_WATER_ACTIVITY_ID)
        ) {
            is Failure -> assertTrue(result.value is GetWaterActivityError.NotAthletesCoach)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `get water activity - not water activity`() {
        val createWaterActivityService = createWaterActivityServices()

        when (
            val result = createWaterActivityService.getWaterActivity(FIRST_COACH_ID, 1)
        ) {
            is Failure -> assertTrue(result.value is GetWaterActivityError.NotWaterActivity)
            is Success -> fail("Unexpected $result")
        }
    }

    /**
     * Remove WaterActivity Test
     */

    @Test
    fun `remove water activity - success`() {
        val createWaterActivityService = createWaterActivityServices()

        when (
            val result = createWaterActivityService.removeWaterActivity(FIRST_COACH_ID, SECOND_WATER_ACTIVITY_ID)
        ) {
            is Failure -> fail("Unexpected $result")
            is Success -> assertTrue(result.value == SECOND_WATER_ACTIVITY_ID)
        }
    }

    @Test
    fun `remove water activity - activity not found`() {
        val createWaterActivityService = createWaterActivityServices()

        when (
            val result = createWaterActivityService.removeWaterActivity(FIRST_COACH_ID, 0)
        ) {
            is Failure -> assertTrue(result.value is RemoveWaterActivityError.ActivityNotFound)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `remove water activity - not athletes coach`() {
        val createWaterActivityService = createWaterActivityServices()

        when (
            val result = createWaterActivityService.removeWaterActivity(SECOND_COACH_ID, FIRST_WATER_ACTIVITY_ID)
        ) {
            is Failure -> assertTrue(result.value is RemoveWaterActivityError.NotAthletesCoach)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `remove water activity - not water activity`() {
        val createWaterActivityService = createWaterActivityServices()

        when (
            val result = createWaterActivityService.removeWaterActivity(FIRST_COACH_ID, NOT_WATER_ACTIVITY_ID)
        ) {
            is Failure -> assertTrue(result.value is RemoveWaterActivityError.NotWaterActivity)
            is Success -> fail("Unexpected $result")
        }
    }

    companion object {
        private const val FIRST_COACH_ID = 1
        private const val SECOND_COACH_ID = 2
        private const val FIRST_ATHLETE_ID = 3
        private const val DATE = "25-05-2025"
        private const val DATE_WITHOUT_MICROCYCLE = "01-01-2000"

        private const val FIRST_WATER_ACTIVITY_ID = 2
        private const val SECOND_WATER_ACTIVITY_ID = 3

        private const val NOT_WATER_ACTIVITY_ID = 1

        private const val PSE = 10
        private const val CONDITION = "good"
        private const val HEART_RATE = 120
        private const val DURATION = 60
        private val MANEUVERS_LIST = listOf(
            WaveInputInfo(
                points = 10f,
                maneuvers = listOf(
                    ManeuverInputInfo(
                        waterManeuverId = 1,
                        rightSide = true,
                        success = true
                    ),
                    ManeuverInputInfo(
                        waterManeuverId = 2,
                        rightSide = false,
                        success = false
                    )
                )
            )
        )

        private fun createWaterActivityServices() = WaterActivityServices(
            JdbiTransactionManager(jdbi),
            WaterActivityDomain()
        )

        private val jdbi =
            Jdbi.create(
                PGSimpleDataSource().apply {
                    setURL("jdbc:postgresql://localhost:5432/db?user=dbuser&password=changeit")
                },
            ).configureWithAppRequirements()

    }
}
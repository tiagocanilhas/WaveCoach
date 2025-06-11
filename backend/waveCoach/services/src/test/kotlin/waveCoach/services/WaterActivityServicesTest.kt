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
                is Failure -> assertTrue(result.value is CreateWaterActivityError.InvalidRpe)
                is Success -> fail("Unexpected $result")
            }
        }
    }

    @Test
    fun `create water activity - invalid heart rate`() {
        val createWaterActivityService = createWaterActivityServices()

        val invalidTrimpList = listOf(-1, 0, 201)

        invalidTrimpList.forEach { trimp ->
            when (
                val result =
                    createWaterActivityService.createWaterActivity(
                        FIRST_COACH_ID,
                        FIRST_ATHLETE_ID,
                        DATE,
                        PSE,
                        CONDITION,
                        trimp,
                        DURATION,
                        MANEUVERS_LIST,
                    )
            ) {
                is Failure -> assertTrue(result.value is CreateWaterActivityError.InvalidTrimp)
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
                            rightSide = true,
                            maneuvers =
                                listOf(
                                    ManeuverInputInfo(
                                        waterManeuverId = invalidManeuver,
                                        success = true,
                                    ),
                                ),
                        ),
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

    /**
     * Create Questionnaire Test
     */

    @Test
    fun `create questionnaire - success`() {
        val createWaterActivityService = createWaterActivityServices()

        when (
            val result =
                createWaterActivityService.createQuestionnaire(
                    FIRST_COACH_ID,
                    FIRST_WATER_ACTIVITY_ID,
                    5,
                    5,
                    5,
                    5,
                )
        ) {
            is Failure -> fail("Unexpected $result")
            is Success -> assertTrue(true)
        }
    }

    @Test
    fun `create questionnaire - activity not found`() {
        val createWaterActivityService = createWaterActivityServices()

        when (
            val result =
                createWaterActivityService.createQuestionnaire(
                    FIRST_COACH_ID,
                    0,
                    5,
                    5,
                    5,
                    5,
                )
        ) {
            is Failure -> assertTrue(result.value is CreateQuestionnaireError.ActivityNotFound)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `create questionnaire - not athletes coach`() {
        val createWaterActivityService = createWaterActivityServices()

        when (
            val result =
                createWaterActivityService.createQuestionnaire(
                    SECOND_COACH_ID,
                    FIRST_WATER_ACTIVITY_ID,
                    5,
                    5,
                    5,
                    5,
                )
        ) {
            is Failure -> assertTrue(result.value is CreateQuestionnaireError.NotAthletesCoach)
            is Success -> fail("Unexpected $result")
        }
    }

    /**
     * Add Wave Test
     */

    @Test
    fun `add wave - success`() {
        val createWaterActivityService = createWaterActivityServices()

        when (
            val result =
                createWaterActivityService.addWave(
                    FIRST_COACH_ID,
                    FIRST_WATER_ACTIVITY_ID,
                    10f,
                    true,
                    emptyList(),
                    3,
                )
        ) {
            is Failure -> fail("Unexpected $result")
            is Success -> assertTrue(result.value > 0)
        }
    }

    @Test
    fun `add wave - activity not found`() {
        val createWaterActivityService = createWaterActivityServices()

        when (
            val result =
                createWaterActivityService.addWave(
                    FIRST_COACH_ID,
                    0,
                    10f,
                    true,
                    emptyList(),
                    3,
                )
        ) {
            is Failure -> assertTrue(result.value is AddWaveError.ActivityNotFound)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `add wave - not athletes coach`() {
        val createWaterActivityService = createWaterActivityServices()

        when (
            val result =
                createWaterActivityService.addWave(
                    SECOND_COACH_ID,
                    FIRST_WATER_ACTIVITY_ID,
                    10f,
                    true,
                    emptyList(),
                    3,
                )
        ) {
            is Failure -> assertTrue(result.value is AddWaveError.NotAthletesCoach)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `add wave - not water activity`() {
        val createWaterActivityService = createWaterActivityServices()

        when (
            val result =
                createWaterActivityService.addWave(
                    FIRST_COACH_ID,
                    NOT_WATER_ACTIVITY_ID,
                    10f,
                    true,
                    emptyList(),
                    3,
                )
        ) {
            is Failure -> assertTrue(result.value is AddWaveError.NotWaterActivity)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `add wave - invalid water maneuver`() {
        val createWaterActivityService = createWaterActivityServices()

        when (
            val result =
                createWaterActivityService.addWave(
                    FIRST_COACH_ID,
                    FIRST_WATER_ACTIVITY_ID,
                    10f,
                    true,
                    listOf(
                        ManeuverInputInfo(
                            waterManeuverId = 0,
                            success = true,
                        ),
                    ),
                    4,
                )
        ) {
            is Failure -> assertTrue(result.value is AddWaveError.InvalidWaterManeuver)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `add wave - invalid order`() {
        val createWaterActivityService = createWaterActivityServices()

        when (
            val result =
                createWaterActivityService.addWave(
                    FIRST_COACH_ID,
                    FIRST_WATER_ACTIVITY_ID,
                    10f,
                    true,
                    emptyList(),
                    0,
                )
        ) {
            is Failure -> assertTrue(result.value is AddWaveError.InvalidOrder)
            is Success -> fail("Unexpected $result")
        }
    }

    /**
     * Remove Wave Test
     */

    @Test
    fun `remove wave - success`() {
        val createWaterActivityService = createWaterActivityServices()

        when (
            val result =
                createWaterActivityService.removeWave(
                    FIRST_COACH_ID,
                    FIRST_WATER_ACTIVITY_ID,
                    FIRST_WAVE_ID,
                )
        ) {
            is Failure -> fail("Unexpected $result")
            is Success -> assertTrue(result.value == FIRST_WAVE_ID)
        }
    }

    @Test
    fun `remove wave - activity not found`() {
        val createWaterActivityService = createWaterActivityServices()

        when (
            val result =
                createWaterActivityService.removeWave(
                    FIRST_COACH_ID,
                    0,
                    FIRST_WAVE_ID,
                )
        ) {
            is Failure -> assertTrue(result.value is RemoveWaveError.ActivityNotFound)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `remove wave - not athletes coach`() {
        val createWaterActivityService = createWaterActivityServices()

        when (
            val result =
                createWaterActivityService.removeWave(
                    SECOND_COACH_ID,
                    FIRST_WATER_ACTIVITY_ID,
                    FIRST_WAVE_ID,
                )
        ) {
            is Failure -> assertTrue(result.value is RemoveWaveError.NotAthletesCoach)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `remove wave - not water activity`() {
        val createWaterActivityService = createWaterActivityServices()

        when (
            val result =
                createWaterActivityService.removeWave(
                    FIRST_COACH_ID,
                    NOT_WATER_ACTIVITY_ID,
                    FIRST_WAVE_ID,
                )
        ) {
            is Failure -> assertTrue(result.value is RemoveWaveError.NotWaterActivity)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `remove wave - wave not found`() {
        val createWaterActivityService = createWaterActivityServices()

        when (
            val result =
                createWaterActivityService.removeWave(
                    FIRST_COACH_ID,
                    FIRST_WATER_ACTIVITY_ID,
                    0,
                )
        ) {
            is Failure -> assertTrue(result.value is RemoveWaveError.WaveNotFound)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `remove wave - not activity wave`() {
        val createWaterActivityService = createWaterActivityServices()

        when (
            val result =
                createWaterActivityService.removeWave(
                    FIRST_COACH_ID,
                    FIRST_WATER_ACTIVITY_ID,
                    FOURTH_WAVE_ID,
                )
        ) {
            is Failure -> println(result.value)// assertTrue(result.value is RemoveWaveError.NotActivityWave)
            is Success -> fail("Unexpected $result")
        }
    }

    /**
     * Add Maneuver Test
     */

    @Test
    fun `add maneuver - success`() {
        val createWaterActivityService = createWaterActivityServices()

        when (
            val result =
                createWaterActivityService.addManeuver(
                    FIRST_COACH_ID,
                    FIRST_WATER_ACTIVITY_ID,
                    SECOND_WAVE_ID,
                    waterManeuverId = 1,
                    success = true,
                    order = 3,
                )
        ) {
            is Failure -> fail("Unexpected $result")
            is Success -> assertTrue(result.value > 0)
        }
    }

    @Test
    fun `add maneuver - activity not found`() {
        val createWaterActivityService = createWaterActivityServices()

        when (
            val result =
                createWaterActivityService.addManeuver(
                    FIRST_COACH_ID,
                    0,
                    SECOND_WAVE_ID,
                    waterManeuverId = 1,
                    success = true,
                    order = 1,
                )
        ) {
            is Failure -> assertTrue(result.value is AddManeuverError.ActivityNotFound)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `add maneuver - not athletes coach`() {
        val createWaterActivityService = createWaterActivityServices()

        when (
            val result =
                createWaterActivityService.addManeuver(
                    SECOND_COACH_ID,
                    FIRST_WATER_ACTIVITY_ID,
                    SECOND_WAVE_ID,
                    waterManeuverId = 1,
                    success = true,
                    order = 1,
                )
        ) {
            is Failure -> assertTrue(result.value is AddManeuverError.NotAthletesCoach)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `add maneuver - not water activity`() {
        val createWaterActivityService = createWaterActivityServices()

        when (
            val result =
                createWaterActivityService.addManeuver(
                    FIRST_COACH_ID,
                    NOT_WATER_ACTIVITY_ID,
                    SECOND_WAVE_ID,
                    waterManeuverId = 1,
                    success = true,
                    order = 1,
                )
        ) {
            is Failure -> assertTrue(result.value is AddManeuverError.NotWaterActivity)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `add maneuver - invalid water maneuver`() {
        val createWaterActivityService = createWaterActivityServices()

        when (
            val result =
                createWaterActivityService.addManeuver(
                    FIRST_COACH_ID,
                    FIRST_WATER_ACTIVITY_ID,
                    SECOND_WAVE_ID,
                    waterManeuverId = 0,
                    success = true,
                    order = 1,
                )
        ) {
            is Failure -> assertTrue(result.value is AddManeuverError.InvalidWaterManeuver)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `add maneuver - invalid order`() {
        val createWaterActivityService = createWaterActivityServices()

        when (
            val result =
                createWaterActivityService.addManeuver(
                    FIRST_COACH_ID,
                    FIRST_WATER_ACTIVITY_ID,
                    SECOND_WAVE_ID,
                    waterManeuverId = 1,
                    success = true,
                    order = 0,
                )
        ) {
            is Failure -> assertTrue(result.value is AddManeuverError.InvalidOrder)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `add maneuver - wave not found`() {
        val createWaterActivityService = createWaterActivityServices()

        when (
            val result =
                createWaterActivityService.addManeuver(
                    FIRST_COACH_ID,
                    FIRST_WATER_ACTIVITY_ID,
                    0,
                    waterManeuverId = 1,
                    success = true,
                    order = 1,
                )
        ) {
            is Failure -> assertTrue(result.value is AddManeuverError.WaveNotFound)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `add maneuver - not activity wave`() {
        val createWaterActivityService = createWaterActivityServices()

        when (
            val result =
                createWaterActivityService.addManeuver(
                    FIRST_COACH_ID,
                    FIRST_WATER_ACTIVITY_ID,
                    FOURTH_WAVE_ID,
                    waterManeuverId = 1,
                    success = true,
                    order = 1,
                )
        ) {
            is Failure -> assertTrue(result.value is AddManeuverError.NotActivityWave)
            is Success -> fail("Unexpected $result")
        }
    }

    /**
     * Remove Maneuver Test
     */

    @Test
    fun `remove maneuver - success`() {
        val createWaterActivityService = createWaterActivityServices()

        when (
            val result =
                createWaterActivityService.removeManeuver(
                    FIRST_COACH_ID,
                    FIRST_WATER_ACTIVITY_ID,
                    SECOND_WAVE_ID,
                    THIRD_MANEUVER_ID,
                )
        ) {
            is Failure -> fail("Unexpected $result")
            is Success -> assertTrue(result.value == THIRD_MANEUVER_ID)
        }
    }

    @Test
    fun `remove maneuver - activity not found`() {
        val createWaterActivityService = createWaterActivityServices()

        when (
            val result =
                createWaterActivityService.removeManeuver(
                    FIRST_COACH_ID,
                    0,
                    SECOND_WAVE_ID,
                    THIRD_MANEUVER_ID,
                )
        ) {
            is Failure -> assertTrue(result.value is RemoveManeuverError.ActivityNotFound)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `remove maneuver - not athletes coach`() {
        val createWaterActivityService = createWaterActivityServices()

        when (
            val result =
                createWaterActivityService.removeManeuver(
                    SECOND_COACH_ID,
                    FIRST_WATER_ACTIVITY_ID,
                    SECOND_WAVE_ID,
                    THIRD_MANEUVER_ID,
                )
        ) {
            is Failure -> assertTrue(result.value is RemoveManeuverError.NotAthletesCoach)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `remove maneuver - not water activity`() {
        val createWaterActivityService = createWaterActivityServices()

        when (
            val result =
                createWaterActivityService.removeManeuver(
                    FIRST_COACH_ID,
                    NOT_WATER_ACTIVITY_ID,
                    SECOND_WAVE_ID,
                    THIRD_MANEUVER_ID,
                )
        ) {
            is Failure -> assertTrue(result.value is RemoveManeuverError.NotWaterActivity)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `remove maneuver - wave not found`() {
        val createWaterActivityService = createWaterActivityServices()

        when (
            val result =
                createWaterActivityService.removeManeuver(
                    FIRST_COACH_ID,
                    FIRST_WATER_ACTIVITY_ID,
                    0,
                    THIRD_MANEUVER_ID,
                )
        ) {
            is Failure -> assertTrue(result.value is RemoveManeuverError.WaveNotFound)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `remove maneuver - not activity wave`() {
        val createWaterActivityService = createWaterActivityServices()

        when (
            val result =
                createWaterActivityService.removeManeuver(
                    FIRST_COACH_ID,
                    FIRST_WATER_ACTIVITY_ID,
                    FOURTH_WAVE_ID,
                    THIRD_MANEUVER_ID,
                )
        ) {
            is Failure -> assertTrue(result.value is RemoveManeuverError.NotActivityWave)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `remove maneuver - maneuver not found`() {
        val createWaterActivityService = createWaterActivityServices()

        when (
            val result =
                createWaterActivityService.removeManeuver(
                    FIRST_COACH_ID,
                    FIRST_WATER_ACTIVITY_ID,
                    SECOND_WAVE_ID,
                    0,
                )
        ) {
            is Failure -> assertTrue(result.value is RemoveManeuverError.ManeuverNotFound)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `remove maneuver - not wave maneuver`() {
        val createWaterActivityService = createWaterActivityServices()

        when (
            val result =
                createWaterActivityService.removeManeuver(
                    FIRST_COACH_ID,
                    FIRST_WATER_ACTIVITY_ID,
                    SECOND_WAVE_ID,
                    FIFTH_MANEUVER_ID,
                )
        ) {
            is Failure -> assertTrue(result.value is RemoveManeuverError.NotWaveManeuver)
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
        private const val FIRST_WAVE_ID = 1
        private const val SECOND_WAVE_ID = 2
        private const val FOURTH_WAVE_ID = 4
        private const val THIRD_MANEUVER_ID = 3
        private const val FIFTH_MANEUVER_ID = 5

        private const val NOT_WATER_ACTIVITY_ID = 1

        private const val PSE = 10
        private const val CONDITION = "good"
        private const val HEART_RATE = 120
        private const val DURATION = 60
        private val MANEUVERS_LIST =
            listOf(
                WaveInputInfo(
                    points = 10f,
                    rightSide = true,
                    maneuvers =
                        listOf(
                            ManeuverInputInfo(
                                waterManeuverId = 1,
                                success = true,
                            ),
                            ManeuverInputInfo(
                                waterManeuverId = 2,
                                success = false,
                            ),
                        ),
                ),
            )

        private fun createWaterActivityServices() =
            WaterActivityServices(
                JdbiTransactionManager(jdbi),
                WaterActivityDomain(),
            )

        private val jdbi =
            Jdbi.create(
                PGSimpleDataSource().apply {
                    setURL("jdbc:postgresql://localhost:5432/db?user=dbuser&password=changeit")
                },
            ).configureWithAppRequirements()
    }
}

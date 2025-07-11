package waveCoach.services

import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.Assertions.assertTrue
import org.postgresql.ds.PGSimpleDataSource
import waveCoach.domain.SetsDomain
import waveCoach.repository.jdbi.JdbiTransactionManager
import waveCoach.repository.jdbi.configureWithAppRequirements
import waveCoach.utils.Failure
import waveCoach.utils.Success
import kotlin.test.Test
import kotlin.test.fail

class GymActivityServicesTest {
    /**
     * Create GymActivity Test
     */

    @Test
    fun `create gym activity - success`() {
        val createGymActivityServices = createGymActivityServices()

        when (
            val result =
                createGymActivityServices.createGymActivity(
                    FIRST_COACH_ID,
                    FIRST_ATHLETE_ID,
                    DATE,
                    EXERCISES_LIST,
                )
        ) {
            is Failure -> fail("Unexpected $result")
            is Success -> assertTrue(result.value > 0)
        }
    }

    @Test
    fun `create gym activity - invalid date`() {
        val createGymActivityServices = createGymActivityServices()

        val invalidDates =
            listOf(
                "32-01-2000",
                "2000-01-01",
            )

        invalidDates.forEach { date ->
            when (
                val result =
                    createGymActivityServices.createGymActivity(
                        FIRST_COACH_ID,
                        FIRST_ATHLETE_ID,
                        date,
                        EXERCISES_LIST,
                    )
            ) {
                is Failure -> assertTrue(result.value is CreateGymActivityError.InvalidDate)
                is Success -> fail("Unexpected $result")
            }
        }
    }

    @Test
    fun `create gym activity - athlete not found`() {
        val createGymActivityServices = createGymActivityServices()

        when (
            val result =
                createGymActivityServices.createGymActivity(
                    FIRST_COACH_ID,
                    0,
                    DATE,
                    EXERCISES_LIST,
                )
        ) {
            is Failure -> assertTrue(result.value is CreateGymActivityError.AthleteNotFound)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `create gym activity - not athlete's coach`() {
        val createGymActivityServices = createGymActivityServices()

        when (
            val result =
                createGymActivityServices.createGymActivity(
                    SECOND_COACH_ID,
                    FIRST_ATHLETE_ID,
                    DATE,
                    EXERCISES_LIST,
                )
        ) {
            is Failure -> assertTrue(result.value is CreateGymActivityError.NotAthletesCoach)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `create gym activity - invalid gym exercise`() {
        val createGymActivityServices = createGymActivityServices()

        val invalidExercisesList =
            listOf(
                ExerciseInputInfo(sets = emptyList(), gymExerciseId = -1),
            )

        when (
            val result =
                createGymActivityServices.createGymActivity(
                    FIRST_COACH_ID,
                    FIRST_ATHLETE_ID,
                    DATE,
                    invalidExercisesList,
                )
        ) {
            is Failure -> assertTrue(result.value is CreateGymActivityError.InvalidGymExercise)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `create gym activity - invalid set`() {
        val createGymActivityServices = createGymActivityServices()

        val invalidExercisesList =
            listOf(
                ExerciseInputInfo(
                    sets = listOf(SetInputInfo(reps = -1, weight = -1f, restTime = -1f)),
                    gymExerciseId = 1
                ),
            )

        when (
            val result =
                createGymActivityServices.createGymActivity(
                    FIRST_COACH_ID,
                    FIRST_ATHLETE_ID,
                    DATE,
                    invalidExercisesList,
                )
        ) {
            is Failure -> assertTrue(result.value is CreateGymActivityError.InvalidSet)
            is Success -> fail("Unexpected $result")
        }
    }

    /**
     * Get Gym Activity Tests
     */

    @Test
    fun `get gym activity - success`() {
        val createGymActivityServices = createGymActivityServices()

        when (val result = createGymActivityServices.getGymActivity(FIRST_COACH_ID, FIRST_GYM_ACTIVITY_ID)) {
            is Failure -> fail("Unexpected $result")
            is Success -> assertTrue(result.value.exercises.isNotEmpty())
        }
    }

    @Test
    fun `get gym activity - activity not found`() {
        val createGymActivityServices = createGymActivityServices()

        when (val result = createGymActivityServices.getGymActivity(FIRST_COACH_ID, 0)) {
            is Failure -> assertTrue(result.value is GetGymActivityError.ActivityNotFound)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `get gym activity - not athlete's coach`() {
        val createGymActivityServices = createGymActivityServices()

        when (val result = createGymActivityServices.getGymActivity(SECOND_COACH_ID, FIRST_GYM_ACTIVITY_ID)) {
            is Failure -> assertTrue(result.value is GetGymActivityError.NotAthletesCoach)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `get gym activity - not gym activity`() {
        val createGymActivityServices = createGymActivityServices()

        when (val result = createGymActivityServices.getGymActivity(FIRST_COACH_ID, THIRD_ACTIVITY_ID)) {
            is Failure -> assertTrue(result.value is GetGymActivityError.NotGymActivity)
            is Success -> fail("Unexpected $result")
        }
    }

    /**
     * Remove Gym Activity Tests
     */

    @Test
    fun `remove gym activity - success`() {
        val createGymActivityServices = createGymActivityServices()

        when (val result = createGymActivityServices.removeGymActivity(FIRST_COACH_ID, SECOND_GYM_ACTIVITY_ID)) {
            is Failure -> fail("Unexpected $result")
            is Success -> assertTrue(result.value == SECOND_GYM_ACTIVITY_ID)
        }
    }

    @Test
    fun `remove gym activity - activity not found`() {
        val createGymActivityServices = createGymActivityServices()

        when (val result = createGymActivityServices.removeGymActivity(FIRST_COACH_ID, 0)) {
            is Failure -> assertTrue(result.value is RemoveGymActivityError.ActivityNotFound)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `remove gym activity - not athlete's coach`() {
        val createGymActivityServices = createGymActivityServices()

        when (val result = createGymActivityServices.removeGymActivity(SECOND_COACH_ID, FIRST_GYM_ACTIVITY_ID)) {
            is Failure -> assertTrue(result.value is RemoveGymActivityError.NotAthletesCoach)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `remove gym activity - not gym activity`() {
        val createGymActivityServices = createGymActivityServices()

        when (val result = createGymActivityServices.removeGymActivity(FIRST_COACH_ID, THIRD_ACTIVITY_ID)) {
            is Failure -> assertTrue(result.value is RemoveGymActivityError.NotGymActivity)
            is Success -> fail("Unexpected $result")
        }
    }

    companion object {
        private const val FIRST_COACH_ID = 1
        private const val SECOND_COACH_ID = 2
        private const val FIRST_ATHLETE_ID = 3
        private const val DATE = "02-08-2025" // date long = 1743801600000

        private const val FIRST_GYM_ACTIVITY_ID = 1
        private const val SECOND_GYM_ACTIVITY_ID = 7
        private const val THIRD_ACTIVITY_ID = 3

        private const val FIRST_EXERCISE_ID = 1
        private const val SECOND_EXERCISE_ID = 2
        private const val THIRD_EXERCISE_ID = 3

        private val EXERCISES_LIST =
            listOf(
                ExerciseInputInfo(
                    sets =
                        listOf(
                            SetInputInfo(
                                reps = 10,
                                weight = 60f,
                                restTime = 60f,
                            ),
                        ),
                    gymExerciseId = 1,
                ),
            )

        private fun createGymActivityServices() =
            GymActivityServices(
                JdbiTransactionManager(jdbi),
                SetsDomain(),
            )

        private val jdbi =
            Jdbi.create(
                PGSimpleDataSource().apply {
                    setURL("jdbc:postgresql://localhost:5432/db?user=dbuser&password=changeit")
                },
            ).configureWithAppRequirements()
    }
}

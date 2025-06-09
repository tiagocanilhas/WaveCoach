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

    /**
     * Add Exercise Tests
     */

    @Test
    fun `add exercise - success`() {
        val createGymActivityServices = createGymActivityServices()

        when (
            val result =
                createGymActivityServices.addExercise(
                    FIRST_COACH_ID,
                    FIRST_GYM_ACTIVITY_ID,
                    sets = emptyList(),
                    gymExerciseId = 1,
                    order = 8
                )
        ) {
            is Failure -> fail("Unexpected $result")
            is Success -> assertTrue(result.value > 0)
        }
    }

    @Test
    fun `add exercise - activity not found`() {
        val createGymActivityServices = createGymActivityServices()

        when (
            val result =
                createGymActivityServices.addExercise(
                    FIRST_COACH_ID,
                    0,
                    sets = emptyList(),
                    gymExerciseId = 1,
                    order = 7
                )
        ) {
            is Failure -> assertTrue(result.value is AddExerciseError.ActivityNotFound)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `add exercise - not athlete's coach`() {
        val createGymActivityServices = createGymActivityServices()

        when (
            val result =
                createGymActivityServices.addExercise(
                    SECOND_COACH_ID,
                    FIRST_GYM_ACTIVITY_ID,
                    sets = emptyList(),
                    gymExerciseId = 1,
                    order = 7
                )
        ) {
            is Failure -> assertTrue(result.value is AddExerciseError.NotAthletesCoach)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `add exercise - not gym activity`() {
        val createGymActivityServices = createGymActivityServices()

        when (
            val result =
                createGymActivityServices.addExercise(
                    FIRST_COACH_ID,
                    THIRD_ACTIVITY_ID,
                    sets = emptyList(),
                    gymExerciseId = 1,
                    order = 7
                )
        ) {
            is Failure -> assertTrue(result.value is AddExerciseError.NotGymActivity)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `add exercise - invalid gym exercise`() {
        val createGymActivityServices = createGymActivityServices()

        when (
            val result =
                createGymActivityServices.addExercise(
                    FIRST_COACH_ID,
                    FIRST_GYM_ACTIVITY_ID,
                    sets = emptyList(),
                    gymExerciseId = -1,
                    order = 7
                )
        ) {
            is Failure -> assertTrue(result.value is AddExerciseError.InvalidGymExercise)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `add exercise - invalid set`() {
        val createGymActivityServices = createGymActivityServices()

        when (
            val result =
                createGymActivityServices.addExercise(
                    FIRST_COACH_ID,
                    FIRST_GYM_ACTIVITY_ID,
                    sets = listOf(SetInputInfo(reps = -1, weight = -1f, restTime = -1f)),
                    gymExerciseId = 1,
                    order = 7
                )
        ) {
            is Failure -> assertTrue(result.value is AddExerciseError.InvalidSet)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `add exercise - invalid order`() {
        val createGymActivityServices = createGymActivityServices()

        when (
            val result =
                createGymActivityServices.addExercise(
                    FIRST_COACH_ID,
                    FIRST_GYM_ACTIVITY_ID,
                    sets = emptyList(),
                    gymExerciseId = 1,
                    order = -1
                )
        ) {
            is Failure -> assertTrue(result.value is AddExerciseError.InvalidOrder)
            is Success -> fail("Unexpected $result")
        }
    }

    /**
     * Remove Exercise Tests
     */

    @Test
    fun `remove exercise - success`() {
        val createGymActivityServices = createGymActivityServices()

        when (
            val result =
                createGymActivityServices.removeExercise(
                    FIRST_COACH_ID,
                    FIRST_GYM_ACTIVITY_ID,
                    FIRST_EXERCISE_ID,
                )
        ) {
            is Failure -> fail("Unexpected $result")
            is Success -> assertTrue(result.value == FIRST_EXERCISE_ID)
        }
    }

    @Test
    fun `remove exercise - activity not found`() {
        val createGymActivityServices = createGymActivityServices()

        when (
            val result =
                createGymActivityServices.removeExercise(
                    FIRST_COACH_ID,
                    0,
                    FIRST_EXERCISE_ID,
                )
        ) {
            is Failure -> assertTrue(result.value is RemoveExerciseError.ActivityNotFound)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `remove exercise - not athlete's coach`() {
        val createGymActivityServices = createGymActivityServices()

        when (
            val result =
                createGymActivityServices.removeExercise(
                    SECOND_COACH_ID,
                    FIRST_GYM_ACTIVITY_ID,
                    SECOND_EXERCISE_ID,
                )
        ) {
            is Failure -> assertTrue(result.value is RemoveExerciseError.NotAthletesCoach)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `remove exercise - not gym activity`() {
        val createGymActivityServices = createGymActivityServices()

        when (
            val result =
                createGymActivityServices.removeExercise(
                    FIRST_COACH_ID,
                    THIRD_ACTIVITY_ID,
                    FIRST_EXERCISE_ID,
                )
        ) {
            is Failure -> assertTrue(result.value is RemoveExerciseError.NotGymActivity)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `remove exercise - exercise not found`() {
        val createGymActivityServices = createGymActivityServices()

        when (
            val result =
                createGymActivityServices.removeExercise(
                    FIRST_COACH_ID,
                    FIRST_GYM_ACTIVITY_ID,
                    0,
                )
        ) {
            is Failure -> assertTrue(result.value is RemoveExerciseError.ExerciseNotFound)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `remove exercise - not exercise in activity`() {
        val createGymActivityServices = createGymActivityServices()

        when (
            val result =
                createGymActivityServices.removeExercise(
                    FIRST_COACH_ID,
                    SECOND_GYM_ACTIVITY_ID,
                    FIRST_EXERCISE_ID,
                )
        ) {
            is Failure -> assertTrue(result.value is RemoveExerciseError.NotActivityExercise)
            is Success -> fail("Unexpected $result")
        }
    }

    /**
     * Add Set Tests
     */

    @Test
    fun `add set - success`() {
        val createGymActivityServices = createGymActivityServices()

        when (
            val result =
                createGymActivityServices.addSet(
                    FIRST_COACH_ID,
                    FIRST_GYM_ACTIVITY_ID,
                    FIRST_EXERCISE_ID,
                    reps = 10,
                    weight = 60f,
                    restTime = 60f,
                    order = 4,
                )
        ) {
            is Failure -> fail("Unexpected $result")
            is Success -> assertTrue(result.value > 0)
        }
    }

    @Test
    fun `add set - activity not found`() {
        val createGymActivityServices = createGymActivityServices()

        when (
            val result =
                createGymActivityServices.addSet(
                    FIRST_COACH_ID,
                    0,
                    FIRST_EXERCISE_ID,
                    reps = 10,
                    weight = 60f,
                    restTime = 60f,
                    order = 4,
                )
        ) {
            is Failure -> assertTrue(result.value is AddSetError.ActivityNotFound)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `add set - not athlete's coach`() {
        val createGymActivityServices = createGymActivityServices()

        when (
            val result =
                createGymActivityServices.addSet(
                    SECOND_COACH_ID,
                    FIRST_GYM_ACTIVITY_ID,
                    SECOND_EXERCISE_ID,
                    reps = 10,
                    weight = 60f,
                    restTime = 60f,
                    order = 4,
                )
        ) {
            is Failure -> assertTrue(result.value is AddSetError.NotAthletesCoach)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `add set - not gym activity`() {
        val createGymActivityServices = createGymActivityServices()

        when (
            val result =
                createGymActivityServices.addSet(
                    FIRST_COACH_ID,
                    THIRD_ACTIVITY_ID,
                    FIRST_EXERCISE_ID,
                    reps = 10,
                    weight = 60f,
                    restTime = 60f,
                    order = 4,
                )
        ) {
            is Failure -> assertTrue(result.value is AddSetError.NotGymActivity)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `add set - exercise not found`() {
        val createGymActivityServices = createGymActivityServices()

        when (
            val result =
                createGymActivityServices.addSet(
                    FIRST_COACH_ID,
                    FIRST_GYM_ACTIVITY_ID,
                    0,
                    reps = 10,
                    weight = 60f,
                    restTime = 60f,
                    order = 4,
                )
        ) {
            is Failure -> assertTrue(result.value is AddSetError.ExerciseNotFound)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `add set - not exercise in activity`() {
        val createGymActivityServices = createGymActivityServices()

        when (
            val result =
                createGymActivityServices.addSet(
                    FIRST_COACH_ID,
                    SECOND_GYM_ACTIVITY_ID,
                    FIRST_EXERCISE_ID,
                    reps = 10,
                    weight = 60f,
                    restTime = 60f,
                    order = 4,
                )
        ) {
            is Failure -> assertTrue(result.value is AddSetError.NotActivityExercise)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `add set - invalid set`() {
        val createGymActivityServices = createGymActivityServices()

        when (
            val result =
                createGymActivityServices.addSet(
                    FIRST_COACH_ID,
                    FIRST_GYM_ACTIVITY_ID,
                    SECOND_EXERCISE_ID,
                    reps = -1,
                    weight = -1f,
                    restTime = -1f,
                    order = 5,
                )
        ) {
            is Failure -> assertTrue(result.value is AddSetError.InvalidSet)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `add set - invalid order`() {
        val createGymActivityServices = createGymActivityServices()

        when (
            val result =
                createGymActivityServices.addSet(
                    FIRST_COACH_ID,
                    FIRST_GYM_ACTIVITY_ID,
                    FIRST_EXERCISE_ID,
                    reps = 10,
                    weight = 60f,
                    restTime = 60f,
                    order = -1,
                )
        ) {
            is Failure -> assertTrue(result.value is AddSetError.InvalidOrder)
            is Success -> fail("Unexpected $result")
        }
    }

    /**
     * Remove Set Tests
     */

    @Test
    fun `remove set - success`() {
        val createGymActivityServices = createGymActivityServices()

        when (
            val result =
                createGymActivityServices.removeSet(
                    FIRST_COACH_ID,
                    FIRST_GYM_ACTIVITY_ID,
                    SECOND_EXERCISE_ID,
                    5,
                )
        ) {
            is Failure -> fail("Unexpected $result")
            is Success -> assertTrue(result.value == 5)
        }
    }

    @Test
    fun `remove set - activity not found`() {
        val createGymActivityServices = createGymActivityServices()

        when (
            val result =
                createGymActivityServices.removeSet(
                    FIRST_COACH_ID,
                    0,
                    FIRST_EXERCISE_ID,
                    5,
                )
        ) {
            is Failure -> assertTrue(result.value is RemoveSetError.ActivityNotFound)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `remove set - not athlete's coach`() {
        val createGymActivityServices = createGymActivityServices()

        when (
            val result =
                createGymActivityServices.removeSet(
                    SECOND_COACH_ID,
                    FIRST_GYM_ACTIVITY_ID,
                    SECOND_EXERCISE_ID,
                    5,
                )
        ) {
            is Failure -> assertTrue(result.value is RemoveSetError.NotAthletesCoach)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `remove set - not gym activity`() {
        val createGymActivityServices = createGymActivityServices()

        when (
            val result =
                createGymActivityServices.removeSet(
                    FIRST_COACH_ID,
                    THIRD_ACTIVITY_ID,
                    FIRST_EXERCISE_ID,
                    5,
                )
        ) {
            is Failure -> assertTrue(result.value is RemoveSetError.NotGymActivity)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `remove set - exercise not found`() {
        val createGymActivityServices = createGymActivityServices()

        when (
            val result =
                createGymActivityServices.removeSet(
                    FIRST_COACH_ID,
                    FIRST_GYM_ACTIVITY_ID,
                    0,
                    5,
                )
        ) {
            is Failure -> assertTrue(result.value is RemoveSetError.ExerciseNotFound)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `remove set - not exercise in activity`() {
        val createGymActivityServices = createGymActivityServices()

        when (
            val result =
                createGymActivityServices.removeSet(
                    FIRST_COACH_ID,
                    SECOND_GYM_ACTIVITY_ID,
                    THIRD_EXERCISE_ID,
                    5,
                )
        ) {
            is Failure -> assertTrue(result.value is RemoveSetError.NotActivityExercise)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `remove set - set not found`() {
        val createGymActivityServices = createGymActivityServices()

        when (
            val result =
                createGymActivityServices.removeSet(
                    FIRST_COACH_ID,
                    FIRST_GYM_ACTIVITY_ID,
                    THIRD_EXERCISE_ID,
                    0,
                )
        ) {
            is Failure -> assertTrue(result.value is RemoveSetError.SetNotFound)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `remove set - not set in exercise`() {
        val createGymActivityServices = createGymActivityServices()

        when (
            val result =
                createGymActivityServices.removeSet(
                    FIRST_COACH_ID,
                    FIRST_GYM_ACTIVITY_ID,
                    THIRD_EXERCISE_ID,
                    4,
                )
        ) {
            is Failure -> assertTrue(result.value is RemoveSetError.NotExerciseSet)
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

package waveCoach.services

import com.cloudinary.Cloudinary
import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import waveCoach.domain.GymExerciseDomain
import waveCoach.repository.jdbi.JdbiTransactionManager
import waveCoach.repository.jdbi.configureWithAppRequirements
import waveCoach.utils.Failure
import waveCoach.utils.Success
import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.fail

class GymExerciseServicesTest {
    /**
     * Create GymExercise Test
     */

    @Test
    fun `create gym exercise - success`() {
        val gymExerciseServices = createGymExerciseServices()

        val name = randomString()
        val category = "shoulders"

        when (val result = gymExerciseServices.createGymExercise(name, category, null)) {
            is Failure -> fail("Unexpected $result")
            is Success -> assertTrue { result.value > 1 }
        }
    }

    @Test
    fun `create gym exercise - invalid name`() {
        val gymExerciseServices = createGymExerciseServices()

        val name = ""
        val category = "shoulders"

        when (val result = gymExerciseServices.createGymExercise(name, category, null)) {
            is Failure -> assertTrue { result.value is CreateGymExerciseError.InvalidName }
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `create gym exercise - invalid category`() {
        val gymExerciseServices = createGymExerciseServices()

        val name = randomString()
        val category = randomString()

        when (val result = gymExerciseServices.createGymExercise(name, category, null)) {
            is Failure -> assertTrue { result.value is CreateGymExerciseError.InvalidCategory }
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `create gym exercise - name already exists`() {
        val gymExerciseServices = createGymExerciseServices()

        val name = GYM_EXERCISE_NAME
        val category = "chest"

        when (val result = gymExerciseServices.createGymExercise(name, category, null)) {
            is Failure -> assertTrue { result.value is CreateGymExerciseError.NameAlreadyExists }
            is Success -> fail("Unexpected $result")
        }
    }

    /**
     * Get All GymExercises Test
     */

    @Test
    fun `get all gym exercises - success`() {
        val gymExerciseServices = createGymExerciseServices()

        val result = gymExerciseServices.getAllGymExercises()
        assertTrue { result.isNotEmpty() }
    }

    /**
     * Update GymExercise Test
     */

    @Test
    fun `update gym exercise - success`() {
        val gymExerciseServices = createGymExerciseServices()

        val name = randomString()
        val category = "shoulders"

        when (val result = gymExerciseServices.updateGymExercise(FIRST_GYM_EXERCISE_ID, name, category)) {
            is Failure -> fail("Unexpected $result")
            is Success -> assertTrue { result.value == FIRST_GYM_EXERCISE_ID }
        }
    }

    @Test
    fun `update gym exercise - invalid name`() {
        val gymExerciseServices = createGymExerciseServices()

        val name = ""
        val category = "shoulders"

        when (val result = gymExerciseServices.updateGymExercise(FIRST_GYM_EXERCISE_ID, name, category)) {
            is Failure -> assertTrue { result.value is UpdateGymExerciseError.InvalidName }
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `update gym exercise - invalid category`() {
        val gymExerciseServices = createGymExerciseServices()

        val name = randomString()
        val category = randomString()

        when (val result = gymExerciseServices.updateGymExercise(FIRST_GYM_EXERCISE_ID, name, category)) {
            is Failure -> assertTrue { result.value is UpdateGymExerciseError.InvalidCategory }
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `update gym exercise - name already exists`() {
        val gymExerciseServices = createGymExerciseServices()

        val name = GYM_EXERCISE_NAME
        val category = "chest"

        when (val result = gymExerciseServices.updateGymExercise(FIRST_GYM_EXERCISE_ID, name, category)) {
            is Failure -> assertTrue { result.value is UpdateGymExerciseError.NameAlreadyExists }
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `update gym exercise - not found`() {
        val gymExerciseServices = createGymExerciseServices()

        val name = randomString()
        val category = "shoulders"

        when (val result = gymExerciseServices.updateGymExercise(0, name, category)) {
            is Failure -> assertTrue { result.value is UpdateGymExerciseError.GymExerciseNotFound }
            is Success -> fail("Unexpected $result")
        }
    }

    /**
     * Remove GymExercise Test
     */

    @Test
    fun `remove gym exercise - success`() {
        val gymExerciseServices = createGymExerciseServices()

        when (val result = gymExerciseServices.removeGymExercise(THIRD_GYM_EXERCISE_ID)) {
            is Failure -> fail("Unexpected $result")
            is Success -> assertTrue { result.value == THIRD_GYM_EXERCISE_ID }
        }
    }

    @Test
    fun `remove gym exercise - not found`() {
        val gymExerciseServices = createGymExerciseServices()

        when (val result = gymExerciseServices.removeGymExercise(0)) {
            is Failure -> assertTrue { result.value is RemoveGymExerciseError.GymExerciseNotFound }
            is Success -> fail("Unexpected $result")
        }
    }

    companion object {
        private fun randomString() = "String_${abs(Random.nextLong())}"

        private const val GYM_EXERCISE_NAME = "Bench Press"
        private const val FIRST_GYM_EXERCISE_ID = 1
        private const val THIRD_GYM_EXERCISE_ID = 3

        private fun createGymExerciseServices() =
            GymExerciseServices(
                JdbiTransactionManager(jdbi),
                GymExerciseDomain(),
                CloudinaryServices(cloudinary),
            )

        private val jdbi =
            Jdbi.create(
                PGSimpleDataSource().apply {
                    setURL("jdbc:postgresql://localhost:5432/db?user=dbuser&password=changeit")
                },
            ).configureWithAppRequirements()

        private val cloudinary =
            Cloudinary(
                mapOf(
                    "cloud_name" to "",
                    "api_key" to "",
                    "api_secret" to "",
                ),
            )
    }
}

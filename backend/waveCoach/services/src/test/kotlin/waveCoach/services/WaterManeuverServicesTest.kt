package waveCoach.services

import com.cloudinary.Cloudinary
import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.postgresql.ds.PGSimpleDataSource
import waveCoach.domain.WaterManeuverDomain
import waveCoach.repository.jdbi.JdbiTransactionManager
import waveCoach.repository.jdbi.configureWithAppRequirements
import waveCoach.utils.Failure
import waveCoach.utils.Success
import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.assertTrue

class WaterManeuverServicesTest {

    /**
     * Create WaterManeuver Test
     */

    @Test
    fun `create water maneuver - success`() {
        val waterManeuversServices = createWaterManeuverServices()

        val name = randomString()

        when(val result = waterManeuversServices.createWaterManeuver(name, null)) {
            is Failure -> fail("Unexpected $result")
            is Success -> assertTrue(result.value > 0)
        }
    }

    @Test
    fun `create water maneuver - invalid name`() {
        val waterManeuversServices = createWaterManeuverServices()

        val name = ""

        when(val result = waterManeuversServices.createWaterManeuver(name, null)) {
            is Failure -> assertTrue(result.value is CreateWaterManeuverError.InvalidName)
            is Success -> fail("Unexpected $result")
        }
    }

    @Test
    fun `create water maneuver - name already exists`() {
        val waterManeuversServices = createWaterManeuverServices()

        val name = randomString()

        when(val result = waterManeuversServices.createWaterManeuver(name, null)) {
            is Failure -> fail("Unexpected $result")
            is Success -> assertTrue(result.value > 0)
        }

        when(val result = waterManeuversServices.createWaterManeuver(name, null)) {
            is Failure -> assertTrue(result.value is CreateWaterManeuverError.NameAlreadyExists)
            is Success -> fail("Unexpected $result")
        }
    }

    /**
     * Get Water Maneuvers Test
     */

    /**
     * Get Water Maneuver by ID Test
     */

    /**
     * Update Water Maneuver Test
     */

    /**
     * Delete Water Maneuver Test
     */

    companion object {
        private fun randomString() = "String_${abs(Random.nextLong())}"

        private fun createWaterManeuverServices() = WaterManeuverServices (
            JdbiTransactionManager(jdbi),
            WaterManeuverDomain(),
            CloudinaryServices(cloudinary),
        )

        private val jdbi =
            Jdbi.create(
                PGSimpleDataSource().apply {
                    setURL("jdbc:postgresql://localhost:5432/db?user=dbuser&password=changeit")
                },
            ).configureWithAppRequirements()

        private val cloudinary = Cloudinary(
            mapOf(
                "cloud_name" to "",
                "api_key" to "",
                "api_secret" to ""
            )
        )

    }
}
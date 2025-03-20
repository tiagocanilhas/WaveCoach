package waveCoach.repository.jdbi

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class JdbiCharacteristicsRepositoryTest {

    @Test
    fun `get characteristics`() = testWithHandleAndRollback { handle ->
        val characteristicsRepository = JdbiCharacteristicsRepository(handle)

        val characteristics =
            characteristicsRepository.getCharacteristics(ATHLETE_ID, ATHLETE_CHARACTERISTICS_FIRST_DATE)

        assertNotNull(characteristics)
    }

    @Test
    fun `store characteristics`() = testWithHandleAndRollback { handle ->
        val characteristicsRepository = JdbiCharacteristicsRepository(handle)

        characteristicsRepository.storeCharacteristics(
            ATHLETE_ID,
            DATE,
            HEIGHT,
            WEIGHT,
            CALORIES,
            WAIST,
            ARM,
            THIGH,
            TRICEP,
            ABDOMINAL
        )

        val characteristics = characteristicsRepository.getCharacteristics(ATHLETE_ID, DATE)

        assertNotNull(characteristics)
    }

    @Test
    fun `get characteristics list`() = testWithHandleAndRollback { handle ->
        val characteristicsRepository = JdbiCharacteristicsRepository(handle)

        val characteristicsList = characteristicsRepository.getCharacteristicsList(ATHLETE_ID)

        assert(characteristicsList.isNotEmpty() && characteristicsList.size == 2)
    }

    @Test
    fun `update characteristics`() = testWithHandleAndRollback { handle ->
        val characteristicsRepository = JdbiCharacteristicsRepository(handle)

        characteristicsRepository.storeCharacteristics(
            ATHLETE_ID,
            DATE,
            HEIGHT,
            WEIGHT,
            CALORIES,
            WAIST,
            ARM,
            THIGH,
            TRICEP,
            ABDOMINAL
        )

        val newHeight = 185
        val newWeight = 85.0f
        val newCalories = 2100
        val newWaist = 95
        val newArm = 35
        val newThigh = 55
        val newTricep = 15.0f
        val newAbdominal = 25.0f

        characteristicsRepository.updateCharacteristics(
            ATHLETE_ID,
            DATE,
            newHeight,
            newWeight,
            newCalories,
            newWaist,
            newArm,
            newThigh,
            newTricep,
            newAbdominal
        )

        val characteristics = characteristicsRepository.getCharacteristics(ATHLETE_ID, DATE)

        assertNotNull(characteristics)

        assert(characteristics.height == newHeight)
        assert(characteristics.weight == newWeight)
        assert(characteristics.calories == newCalories)
        assert(characteristics.waist == newWaist)
        assert(characteristics.arm == newArm)
        assert(characteristics.thigh == newThigh)
        assert(characteristics.tricep == newTricep)
        assert(characteristics.abdominal == newAbdominal)
    }

    @Test
    fun `remove characteristics`() = testWithHandleAndRollback { handle ->
        val characteristicsRepository = JdbiCharacteristicsRepository(handle)

        characteristicsRepository.storeCharacteristics(
            ATHLETE_ID,
            DATE,
            HEIGHT,
            WEIGHT,
            CALORIES,
            WAIST,
            ARM,
            THIGH,
            TRICEP,
            ABDOMINAL
        )

        characteristicsRepository.removeCharacteristics(ATHLETE_ID, DATE)

        val characteristics = characteristicsRepository.getCharacteristics(ATHLETE_ID, DATE)

        assertNull(characteristics)
    }

    companion object {
        private const val ATHLETE_ID = 3
        private const val ATHLETE_CHARACTERISTICS_FIRST_DATE = 948758400000 // 25-01-2000
        private const val DATE = 1620000000000 // 2021-05-03
        private const val HEIGHT = 180
        private const val WEIGHT = 80.0f
        private const val CALORIES = 2000
        private const val WAIST = 90
        private const val ARM = 30
        private const val THIGH = 50
        private const val TRICEP = 10.0f
        private const val ABDOMINAL = 20.0f
    }
}
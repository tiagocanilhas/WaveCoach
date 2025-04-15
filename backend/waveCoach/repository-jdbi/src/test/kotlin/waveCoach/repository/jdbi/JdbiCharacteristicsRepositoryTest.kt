package waveCoach.repository.jdbi

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class JdbiCharacteristicsRepositoryTest {
    @Test
    fun `get characteristics`() =
        testWithHandleAndRollback { handle ->
            val characteristicsRepository = JdbiCharacteristicsRepository(handle)

            val characteristics =
                characteristicsRepository.getCharacteristics(ATHLETE_ID, ATHLETE_CHARACTERISTICS_FIRST_DATE)

            assertNotNull(characteristics)
        }

    @Test
    fun `store characteristics`() =
        testWithHandleAndRollback { handle ->
            val characteristicsRepository = JdbiCharacteristicsRepository(handle)

            characteristicsRepository.storeCharacteristics(
                ATHLETE_ID,
                DATE,
                HEIGHT,
                WEIGHT,
                CALORIES,
                BODY_FAT,
                WAIST_SIZE,
                ARM_SIZE,
                THIGH_SIZE,
                TRICEP_FAT,
                ABDOMEN_FAT,
                THIGH_FAT,
            )

            val characteristics = characteristicsRepository.getCharacteristics(ATHLETE_ID, DATE)

            assertNotNull(characteristics)
        }

    @Test
    fun `get characteristics list`() =
        testWithHandleAndRollback { handle ->
            val characteristicsRepository = JdbiCharacteristicsRepository(handle)

            val characteristicsList = characteristicsRepository.getCharacteristicsList(ATHLETE_ID)

            assert(characteristicsList.isNotEmpty() && characteristicsList.size == 2)
        }

    @Test
    fun `update characteristics`() =
        testWithHandleAndRollback { handle ->
            val characteristicsRepository = JdbiCharacteristicsRepository(handle)

            characteristicsRepository.storeCharacteristics(
                ATHLETE_ID,
                DATE,
                HEIGHT,
                WEIGHT,
                CALORIES,
                BODY_FAT,
                WAIST_SIZE,
                ARM_SIZE,
                THIGH_SIZE,
                TRICEP_FAT,
                ABDOMEN_FAT,
                THIGH_FAT,
            )

            val newHeight = 185
            val newWeight = 85.0f
            val newCalories = 2100
            val newBodyFat = 17.0f
            val newWaistSize = 95
            val newArmSize = 35
            val newThighSize = 55
            val newTricepFat = 15
            val newAbdomenFat = 25
            val newThighFat = 35

            characteristicsRepository.updateCharacteristics(
                ATHLETE_ID,
                DATE,
                newHeight,
                newWeight,
                newCalories,
                newBodyFat,
                newWaistSize,
                newArmSize,
                newThighSize,
                newTricepFat,
                newAbdomenFat,
                newThighFat,
            )

            val characteristics = characteristicsRepository.getCharacteristics(ATHLETE_ID, DATE)

            assertNotNull(characteristics)

            assert(characteristics.height == newHeight)
            assert(characteristics.weight == newWeight)
            assert(characteristics.calories == newCalories)
            assert(characteristics.waistSize == newWaistSize)
            assert(characteristics.armSize == newArmSize)
            assert(characteristics.thighSize == newThighSize)
            assert(characteristics.tricepFat == newTricepFat)
            assert(characteristics.abdomenFat == newAbdomenFat)
            assert(characteristics.thighFat == newThighFat)
        }

    @Test
    fun `remove characteristics`() =
        testWithHandleAndRollback { handle ->
            val characteristicsRepository = JdbiCharacteristicsRepository(handle)

            characteristicsRepository.storeCharacteristics(
                ATHLETE_ID,
                DATE,
                HEIGHT,
                WEIGHT,
                CALORIES,
                BODY_FAT,
                WAIST_SIZE,
                ARM_SIZE,
                THIGH_SIZE,
                TRICEP_FAT,
                ABDOMEN_FAT,
                THIGH_FAT,
            )

            characteristicsRepository.removeCharacteristics(ATHLETE_ID, DATE)

            val characteristics = characteristicsRepository.getCharacteristics(ATHLETE_ID, DATE)

            assertNull(characteristics)
        }

    @Test
    fun `remove characteristics without date`() =
        testWithHandleAndRollback { handle ->
            val characteristicsRepository = JdbiCharacteristicsRepository(handle)

            characteristicsRepository.storeCharacteristics(
                ATHLETE_ID,
                DATE,
                HEIGHT,
                WEIGHT,
                CALORIES,
                BODY_FAT,
                WAIST_SIZE,
                ARM_SIZE,
                THIGH_SIZE,
                TRICEP_FAT,
                ABDOMEN_FAT,
                THIGH_FAT,
            )

            characteristicsRepository.removeCharacteristicsWithoutDate(ATHLETE_ID)

            val characteristicsList = characteristicsRepository.getCharacteristicsList(ATHLETE_ID)

            assert(characteristicsList.isEmpty())
        }

    companion object {
        private const val ATHLETE_ID = 3
        private const val ATHLETE_CHARACTERISTICS_FIRST_DATE = 948758400000 // 25-01-2000
        private const val DATE = 1620000000000 // 2021-05-03
        private const val HEIGHT = 180
        private const val WEIGHT = 80.0f
        private const val CALORIES = 2000
        private const val BODY_FAT = 15.0f
        private const val WAIST_SIZE = 90
        private const val ARM_SIZE = 30
        private const val THIGH_SIZE = 50
        private const val TRICEP_FAT = 10
        private const val ABDOMEN_FAT = 20
        private const val THIGH_FAT = 30
    }
}

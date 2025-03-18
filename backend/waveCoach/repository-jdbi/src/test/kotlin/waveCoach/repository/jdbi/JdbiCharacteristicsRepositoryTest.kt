package waveCoach.repository.jdbi

import kotlin.test.Test
import kotlin.test.assertNotNull

class JdbiCharacteristicsRepositoryTest {

    @Test
    fun `store and get characteristics`() = testWithHandleAndRollback { handle ->
        val characteristicsRepository = JdbiCharacteristicsRepository(handle)

        val date = 1620000000000 // 2021-05-03
        val height = 180
        val weight = 80.0f
        val calories = 2000
        val waist = 90
        val arm = 30
        val thigh = 50
        val tricep = 10.0f
        val abdominal = 20.0f

        characteristicsRepository.storeCharacteristics(
            ATHELETE_ID,
            date,
            height,
            weight,
            calories,
            waist,
            arm,
            thigh,
            tricep,
            abdominal
        )

        val characteristics = characteristicsRepository.getCharacteristics(ATHELETE_ID, date)

        assertNotNull(characteristics)
    }


    companion object {
        const val ATHELETE_ID = 3
    }
}
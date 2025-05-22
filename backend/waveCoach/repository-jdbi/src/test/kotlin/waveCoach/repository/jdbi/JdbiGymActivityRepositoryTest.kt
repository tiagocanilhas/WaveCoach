package waveCoach.repository.jdbi

import waveCoach.domain.Category
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class JdbiGymActivityRepositoryTest {
    @Test
    fun `store Gym activity`() =
        testWithHandleAndRollback { handle ->
            val gymActivityRepository = JdbiGymActivityRepository(handle)
            val activityRepository = JdbiActivityRepository(handle)

            val activityId = activityRepository.storeActivity(FIRST_ATHLETE_ID, DATE, MICRO_ID)

            val gymActivityId = gymActivityRepository.storeGymActivity(activityId)

            assertEquals(activityId, gymActivityId)
        }

    @Test
    fun `get Gym activities`() =
        testWithHandleAndRollback { handle ->
            val gymActivityRepository = JdbiGymActivityRepository(handle)

            val gymActivityList = gymActivityRepository.getGymActivityList(FIRST_ATHLETE_ID)

            assertTrue { gymActivityList.isNotEmpty() }
        }

    @Test
    fun `remove Gym activities`() =
        testWithHandleAndRollback { handle ->
            val gymActivityRepository = JdbiGymActivityRepository(handle)

            gymActivityRepository.removeGymActivities(SECOND_ATHLETE_ID)

            val gymActivityList = gymActivityRepository.getGymActivityList(SECOND_ATHLETE_ID)

            assertTrue { gymActivityList.isEmpty() }
        }

    @Test
    fun `remove Gym activity`() =
        testWithHandleAndRollback { handle ->
            val gymActivityRepository = JdbiGymActivityRepository(handle)

            gymActivityRepository.removeGymActivity(SECOND_ATHLETE_ID)

            val gymActivityList = gymActivityRepository.getGymActivityList(SECOND_ATHLETE_ID)

            assertTrue { gymActivityList.isEmpty() }
        }

    @Test
    fun `store and get exercise`() =
        testWithHandleAndRollback { handle ->
            val gymActivityRepository = JdbiGymActivityRepository(handle)
            val activityRepository = JdbiActivityRepository(handle)

            val activityId = activityRepository.storeActivity(FIRST_ATHLETE_ID, DATE, MICRO_ID)

            val gymActivityId = gymActivityRepository.storeGymActivity(activityId)

            val exerciseId = gymActivityRepository.storeExercise(gymActivityId, 1, 1)

            val exerciseList = gymActivityRepository.getExercises(gymActivityId)

            assertTrue { exerciseList.isNotEmpty() }

            assertEquals(exerciseId, exerciseList[0].id)

        }

    @Test
    fun `get exercises `() =
        testWithHandleAndRollback { handle ->
            val gymActivityRepository = JdbiGymActivityRepository(handle)

            val exerciseList = gymActivityRepository.getExercises(FIRST_GYM_ACTIVITY_ID)

            assertTrue { exerciseList.isNotEmpty() }
        }

    @Test
    fun `remove exercises by athlete`() =
        testWithHandleAndRollback { handle ->
            val gymActivityRepository = JdbiGymActivityRepository(handle)

            gymActivityRepository.removeExercisesByAthlete(SECOND_ATHLETE_ID)

            val exerciseList = gymActivityRepository.getExercises(FOURTH_GYM_ACTIVITY_ID)

            assertTrue { exerciseList.isEmpty() }
        }

    @Test
    fun `remove exercises by activity`() =
        testWithHandleAndRollback { handle ->
            val gymActivityRepository = JdbiGymActivityRepository(handle)

            gymActivityRepository.removeExercisesByActivity(SECOND_GYM_ACTIVITY_ID)

            val exerciseList = gymActivityRepository.getExercises(SECOND_GYM_ACTIVITY_ID)

            assertTrue { exerciseList.isEmpty() }
        }

    @Test
    fun `store and get sets`() =
        testWithHandleAndRollback { handle ->
            val gymActivityRepository = JdbiGymActivityRepository(handle)
            val activityRepository = JdbiActivityRepository(handle)

            val activityId = activityRepository.storeActivity(FIRST_ATHLETE_ID, DATE, MICRO_ID)

            val gymActivityId = gymActivityRepository.storeGymActivity(activityId)

            val exerciseId = gymActivityRepository.storeExercise(gymActivityId, 1, 1)

            val setId = gymActivityRepository.storeSet(exerciseId, 10, 20f, 30f, 1)
            val setId2 = gymActivityRepository.storeSet(exerciseId, 15, 25f, 35f, 2)

            val setList = gymActivityRepository.getSets(exerciseId)

            assertTrue { setList.isNotEmpty() }

            assertEquals(setId, setList[0].id)
            assertEquals(setId2, setList[1].id)
        }

    @Test
    fun `get sets`() =
        testWithHandleAndRollback { handle ->
            val gymActivityRepository = JdbiGymActivityRepository(handle)

            val setList = gymActivityRepository.getSets(FIRST_EXERCISE_ID)

            assertTrue { setList.isNotEmpty() }
        }

    @Test
    fun `remove sets by athlete`() =
        testWithHandleAndRollback { handle ->
            val gymActivityRepository = JdbiGymActivityRepository(handle)

            gymActivityRepository.removeSetsByAthlete(FIRST_ATHLETE_ID)

            val setList = gymActivityRepository.getSets(FIRST_EXERCISE_ID)

            assertTrue { setList.isEmpty() }
        }

    @Test
    fun `remove sets by activity`() =
        testWithHandleAndRollback { handle ->
            val gymActivityRepository = JdbiGymActivityRepository(handle)

            gymActivityRepository.removeSetsByActivity(FIRST_GYM_ACTIVITY_ID)

            val setList = gymActivityRepository.getSets(FIRST_EXERCISE_ID)

            assertTrue { setList.isEmpty() }
        }

    @Test
    fun `store and get Gym exercise`() =
        testWithHandleAndRollback { handle ->
            val gymActivityRepository = JdbiGymActivityRepository(handle)

            val gymExerciseId = gymActivityRepository.storeGymExercise("abc", "chest", null)

            val gymExercise = gymActivityRepository.getGymExerciseByName("abc")

            assertTrue { gymExercise != null }
            assertEquals(gymExerciseId, gymExercise?.id)
        }

    @Test
    fun `get Gym exercise by name`() =
        testWithHandleAndRollback { handle ->
            val gymActivityRepository = JdbiGymActivityRepository(handle)

            val gymExercise = gymActivityRepository.getGymExerciseByName("Leg Press")

            assertTrue { gymExercise != null }
            assertEquals(3, gymExercise?.id)
            assertEquals("Leg Press", gymExercise?.name)
            assertEquals(Category.LEGS, gymExercise?.category)
        }

    @Test
    fun `get all Gym exercises`() =
        testWithHandleAndRollback { handle ->
            val gymActivityRepository = JdbiGymActivityRepository(handle)

            val gymExerciseList = gymActivityRepository.getAllGymExercises()

            assertTrue { gymExerciseList.isNotEmpty() }
        }

    @Test
    fun `update Gym exercise`() =
        testWithHandleAndRollback { handle ->
            val gymActivityRepository = JdbiGymActivityRepository(handle)

            gymActivityRepository.updateGymExercise(GYM_EXERCISE_ID, "Push Up", "Chest")

            val updatedGymExercise = gymActivityRepository.getGymExerciseByName("Push Up")

            assertTrue { updatedGymExercise != null }
        }

    @Test
    fun `remove Gym exercise`() =
        testWithHandleAndRollback { handle ->
            val gymActivityRepository = JdbiGymActivityRepository(handle)

            gymActivityRepository.removeGymExercise(GYM_EXERCISE_ID)

            val removedGymExercise = gymActivityRepository.getGymExerciseByName("Leg Press")

            assertTrue { removedGymExercise == null }
        }

    @Test
    fun `is Gym exercise valid`() =
        testWithHandleAndRollback { handle ->
            val gymActivityRepository = JdbiGymActivityRepository(handle)

            val isValid = gymActivityRepository.isGymExerciseValid(1)
            val isNotValid = gymActivityRepository.isGymExerciseValid(-1)

            assertTrue { isValid }
            assertFalse { isNotValid }
        }

    companion object {
        private const val DATE = 1743801600000 // (02-08-2025)
        private const val MICRO_ID = 4
        private const val FIRST_ATHLETE_ID = 3
        private const val SECOND_ATHLETE_ID = 4
        private const val THIRD_ATHLETE_ID = 5
        private const val GYM_EXERCISE_ID = 3
        private const val FIRST_GYM_ACTIVITY_ID = 1
        private const val FIRST_EXERCISE_ID = 1
        private const val SECOND_GYM_ACTIVITY_ID = 2
        private const val FOURTH_GYM_ACTIVITY_ID = 4
    }
}

package waveCoach.repository

import waveCoach.domain.*

interface GymActivityRepository {
    fun storeGymActivity(activityId: Int): Int

    fun getGymActivityList(uid: Int): List<GymActivity>

    fun removeGymActivities(uid: Int)

    fun removeGymActivity(activityId: Int)

    fun storeExercise(activityID: Int, exerciseID: Int, exerciseOrder: Int): Int

    fun storeExercises(exercises: List<ExerciseToInsert>): List<Int>

    fun getExercises(activityId: Int): List<Exercise>

    fun getExerciseById(exerciseId: Int): Exercise?

    fun removeExercisesByAthlete(athleteId: Int)

    fun removeExercisesByActivity(activityId: Int)

    fun removeExerciseById(exerciseId: Int)

    fun verifyExerciseOrder(activityId: Int, exerciseOrder: Int): Boolean

    fun storeSet(exerciseId: Int, reps: Int, weight: Float, rest: Float, setOrder: Int): Int

    fun storeSets(sets: List<SetToInsert>): List<Int>

    fun getSets(exerciseId: Int): List<Sets>

    fun getSetById(setId: Int): Sets?

    fun removeSetsByAthlete(athleteId: Int)

    fun removeSetsByActivity(activityId: Int)

    fun removeSetById(setId: Int)

    fun setBelongsToExercise(exerciseId: Int, setId: Int): Boolean

    fun verifySetOrder(exerciseId: Int, setOrder: Int): Boolean

    fun storeGymExercise(name: String, category: String, url: String?): Int

    fun getGymExerciseByName(name: String): GymExercise?

    fun getAllGymExercises(): List<GymExercise>

    fun updateGymExercise(exerciseId: Int, name: String, category: String)

    fun removeGymExercise(exerciseId: Int)

    fun isGymExerciseValid(exerciseId: Int): Boolean
}

package waveCoach.repository

import waveCoach.domain.Exercise
import waveCoach.domain.GymActivity
import waveCoach.domain.GymExercise
import waveCoach.domain.Sets

interface GymActivityRepository {
    fun storeGymActivity(activityId: Int): Int

    fun getGymActivityList(uid: Int): List<GymActivity>

    fun removeGymActivities(uid: Int)

    fun storeExercise(activityID: Int, exerciseID: Int, exerciseOrder: Int): Int

    fun getExercises(activityId: Int): List<Exercise>

    fun removeExercisesByAthlete(athleteId: Int)

    fun storeSet(exerciseId: Int, reps: Int, weight: Float, rest: Float, setOrder: Int): Int

    fun getSets(exerciseId: Int): List<Sets>

    fun removeSetsByAthlete(athleteId: Int)

    fun storeGymExercise(name: String, category: String): Int

    fun getGymExerciseByName(name: String): GymExercise?

    fun getAllGymExercises(): List<GymExercise>

    fun updateGymExercise(exerciseId: Int, name: String, category: String)

    fun removeGymExercise(exerciseId: Int)

    fun isGymExerciseValid(exerciseId: Int): Boolean

}

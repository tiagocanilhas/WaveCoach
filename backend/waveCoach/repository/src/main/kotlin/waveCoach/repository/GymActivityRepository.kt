package waveCoach.repository

import waveCoach.domain.GymActivity

interface GymActivityRepository {
    fun storeGymActivity(activityId: Int): Int

    fun getGymActivities(uid: Int): List<GymActivity>

    fun removeGymActivities(uid: Int)
}

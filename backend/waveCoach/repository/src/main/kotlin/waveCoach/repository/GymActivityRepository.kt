package waveCoach.repository

interface GymActivityRepository {
    fun storeGymActivity(activityId: Int): Int
}
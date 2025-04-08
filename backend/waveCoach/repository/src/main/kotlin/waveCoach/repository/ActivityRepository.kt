package waveCoach.repository

import waveCoach.domain.Activity

interface ActivityRepository {
    fun storeActivity(uid: Int, date: Long): Int
    //fun getActivityList(uid: Int, date: Long): List<Activity>
    fun getAthleteActivityList(uid: Int): List<Activity>
    fun removeActivities(uid: Int)
}
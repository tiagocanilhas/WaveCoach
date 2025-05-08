package waveCoach.repository

import waveCoach.domain.Activity
import waveCoach.domain.ActivityWithExercises

interface ActivityRepository {
    fun storeActivity(uid: Int, date: Long, type: String): Int

    // fun getActivityList(uid: Int, date: Long): List<Activity>
    fun getAthleteActivityList(uid: Int): List<Activity>

    fun getActivity(uid: Int): Activity?

    fun getActivityById(activityId: Int): Activity?

    fun removeActivities(uid: Int)
}

package waveCoach.repository

import waveCoach.domain.Activity
import waveCoach.domain.ActivityType
import waveCoach.domain.Mesocycle
import waveCoach.domain.Microcycle

interface ActivityRepository {
    fun storeActivity(
        uid: Int,
        date: Long,
        microcycle: Int,
    ): Int

    fun storeMesocycle(
        uid: Int,
        startTime: Long,
        endTime: Long,
    ): Int

    fun getMesocycle(id: Int): Mesocycle?

    fun updateMesocycle(
        id: Int,
        startTime: Long,
        endTime: Long,
    ): Int

    fun removeMesocycles(uid: Int)

    fun storeMicrocycle(
        mesocycle: Int,
        startTime: Long,
        endTime: Long,
    ): Int

    fun getMicrocycle(id: Int): Microcycle?

    fun getMicrocycleByDate(
        date: Long,
        uid: Int,
    ): Microcycle?

    fun updateMicrocycle(
        id: Int,
        startTime: Long,
        endTime: Long,
    ): Int

    fun removeMicrocycles(uid: Int)

    fun getCalendar(
        uid: Int,
        type: ActivityType?,
    ): List<Mesocycle>

    fun getAthleteActivityList(uid: Int): List<Activity>

    fun getActivity(uid: Int): Activity?

    fun getActivityById(activityId: Int): Activity?

    fun removeActivities(uid: Int)

    fun removeActivity(activityId: Int)
}

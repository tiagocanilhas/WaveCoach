package waveCoach.repository

interface ActivityRepository {
    fun storeActivity(uid: Int, date: Long): Int
}
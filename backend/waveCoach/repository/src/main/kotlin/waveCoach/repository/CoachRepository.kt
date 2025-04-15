package waveCoach.repository

interface CoachRepository {
    fun storeCoach(uid: Int): Int
}

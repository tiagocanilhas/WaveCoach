package waveCoach.repository

interface AthleteRepository {
    fun storeAthlete(uid: Int, coachId: Int, name: String, birthDate: String): Int
}
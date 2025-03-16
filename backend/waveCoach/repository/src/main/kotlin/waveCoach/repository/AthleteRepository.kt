package waveCoach.repository

import java.sql.Date

interface AthleteRepository {
    fun storeAthlete(uid: Int, coachId: Int, name: String, birthDate: Long): Int
}
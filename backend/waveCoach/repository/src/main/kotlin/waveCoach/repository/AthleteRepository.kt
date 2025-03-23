package waveCoach.repository

import waveCoach.domain.Athlete

interface AthleteRepository {
    fun storeAthlete(uid: Int, coachId: Int, name: String, birthDate: Long): Int

    fun getAthlete(uid: Int): Athlete?

    fun getAthleteList(coachId: Int): List<Athlete>

    fun updateAthlete(uid: Int, name: String, birthDate: Long)

    fun removeAthlete(uid: Int)
}
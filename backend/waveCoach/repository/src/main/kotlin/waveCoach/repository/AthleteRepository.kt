package waveCoach.repository

import waveCoach.domain.Athlete
import waveCoach.domain.AthleteCode
import waveCoach.domain.CodeValidationInfo

interface AthleteRepository {
    fun storeAthlete(
        uid: Int,
        coachId: Int,
        name: String,
        birthdate: Long,
        url: String?,
    ): Int

    fun getAthlete(uid: Int): Athlete?

    fun getAthleteList(coachId: Int): List<Athlete>

    fun updateAthlete(
        uid: Int,
        name: String?,
        birthdate: Long?,
        url: String?,
    )

    fun setCredentialsChangedToTrue(uid: Int)

    fun removeAthlete(uid: Int)

    fun storeCode(code: AthleteCode)

    fun getByCode(code: CodeValidationInfo): Triple<Athlete, String, Long>?

    fun removeCode(uid: Int)
}

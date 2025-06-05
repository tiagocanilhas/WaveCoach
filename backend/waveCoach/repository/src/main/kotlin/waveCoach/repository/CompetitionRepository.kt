package waveCoach.repository

interface CompetitionRepository {
    fun storeCompetition(
        uid: Int,
        date: Long,
        location: String
    ): Int

    fun competitionExists(
        id: Int
    ): Boolean

}
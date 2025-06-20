package waveCoach.repository

import waveCoach.domain.CompetitionWithHeats
import waveCoach.domain.HeatToInsert

interface CompetitionRepository {
    fun storeCompetition(uid: Int, date: Long, location: String, place: Int): Int

    fun getCompetition(id: Int): CompetitionWithHeats?

    fun competitionExists(id: Int): Boolean

    fun removeCompetition(id: Int)

    fun storeHeats(heats: List<HeatToInsert>): List<Int>
}
package waveCoach.repository

import waveCoach.domain.CompetitionWithHeats
import waveCoach.domain.Heat
import waveCoach.domain.HeatToInsert
import waveCoach.domain.HeatToUpdate

interface CompetitionRepository {
    fun storeCompetition(uid: Int, date: Long, location: String, place: Int, name: String): Int

    fun getCompetition(id: Int): CompetitionWithHeats?

    fun updateCompetition(id: Int, date: Long?, location: String?, place: Int?, name: String?)

    fun competitionExists(id: Int): Boolean

    fun removeCompetition(id: Int)

    fun storeHeats(heats: List<HeatToInsert>): List<Int>

    fun getHeatsByCompetition(competitionId: Int): List<Heat>

    fun updateHeats(heats: List<HeatToUpdate>)

    fun removeHeatsById(heatIds: List<Int>)
}
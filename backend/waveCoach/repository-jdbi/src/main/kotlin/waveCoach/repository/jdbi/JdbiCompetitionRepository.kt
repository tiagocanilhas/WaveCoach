package waveCoach.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import waveCoach.domain.*
import waveCoach.repository.CompetitionRepository

class JdbiCompetitionRepository(
    private val handle: Handle,
) : CompetitionRepository {
    override fun storeCompetition(
        uid: Int,
        date: Long,
        location: String,
        place: Int,
        name: String,
    ): Int {
        return handle.createUpdate(
            """
            insert into waveCoach.competition (uid, date, location, place, name)
            values (:uid, :date, :location, :place, :name)
            """.trimIndent(),
        )
            .bind("uid", uid)
            .bind("date", date)
            .bind("location", location)
            .bind("place", place)
            .bind("name", name)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .one()
    }

    private data class Row(
        val id: Int,
        val uid: Int,
        val competitionDate: Long,
        val location: String,
        val place: Int,
        val name: String,
        val heatId: Int?,
        val score: Float?,
        val waterActivityId: Int?,
        val date: Long?,
        val athleteId: Int?,
        val microcycle: Int?,
        val rpe: Int?,
        val condition: String?,
        val trimp: Int?,
        val duration: Int?,
        val waveId: Int?,
        val points: Float?,
        val rightSide: Boolean?,
        val waveOrder: Int?,
        val maneuverId: Int?,
        val waterManeuverId: Int?,
        val waterManeuverName: String?,
        val url: String?,
        val success: Boolean?,
        val maneuverOrder: Int?,
    )

    override fun getCompetition(id: Int): CompetitionWithHeats? {
        val query =
            """
            select c.id, c.uid, c.date as competition_date, c.location, c.place, c.name, h.id as heat_id, h.score, 
            wa.activity as water_activity_id, a.uid as athlete_id, a.microcycle, wa.rpe, wa.condition, wa.trimp, 
            wa.duration, w.id as wave_id, w.points, w.right_side, w.wave_order, m.id as maneuver_id, 
            wm.id as water_maneuver_id, wm.name as water_maneuver_name, wm.url, m.success, m.maneuver_order
            from waveCoach.competition c
            left join waveCoach.heat h on c.id = h.competition
            left join waveCoach.water wa on h.water_activity = wa.activity
            left join waveCoach.activity a on wa.activity = a.id
            left join waveCoach.wave w on wa.activity = w.activity
            left join waveCoach.maneuver m on w.id = m.wave
            left join waveCoach.water_maneuver wm on m.maneuver = wm.id
            where c.id = :id
            """.trimIndent()

        val rows =
            handle.createQuery(query)
                .bind("id", id)
                .mapTo<Row>()
                .list()

        val competition = rows.firstOrNull() ?: return null

        return CompetitionWithHeats(
            id = competition.id,
            uid = competition.uid,
            date = competition.competitionDate,
            location = competition.location,
            place = competition.place,
            name = competition.name,
            heats =
                rows.groupBy { it.heatId }.filterKeys { it != null }.map { (_, heatRows) ->
                    val heatInfo = heatRows.first()
                    HeatWithWaterActivity(
                        id = heatInfo.heatId!!,
                        score = heatInfo.score ?: 0f,
                        waterActivity =
                            WaterActivityWithWaves(
                                id = heatInfo.waterActivityId!!,
                                athleteId = heatInfo.athleteId ?: 0,
                                date = heatInfo.date ?: 0L,
                                microcycleId = heatInfo.microcycle ?: 0,
                                rpe = heatInfo.rpe ?: 0,
                                condition = heatInfo.condition ?: "",
                                trimp = heatInfo.trimp ?: 0,
                                duration = heatInfo.duration ?: 0,
                                waves =
                                    heatRows.groupBy { it.waveId }.filterKeys { it != null }.map { (_, waveRows) ->
                                        val waveInfo = waveRows.first()
                                        WaveWithManeuvers(
                                            id = waveInfo.waveId!!,
                                            points = waveInfo.points ?: 0f,
                                            rightSide = waveInfo.rightSide ?: false,
                                            order = waveInfo.waveOrder ?: 0,
                                            maneuvers =
                                                waveRows.mapNotNull { maneuverRow ->
                                                    if (maneuverRow.maneuverId != null) {
                                                        Maneuver(
                                                            id = maneuverRow.maneuverId!!,
                                                            wave = waveInfo.waveId!!,
                                                            waterManeuverId =
                                                                maneuverRow.waterManeuverId ?: 0,
                                                            waterManeuverName =
                                                                maneuverRow.waterManeuverName ?: "",
                                                            url = maneuverRow.url ?: "",
                                                            success = maneuverRow.success ?: false,
                                                            maneuverOrder =
                                                                maneuverRow.maneuverOrder ?: 0,
                                                        )
                                                    } else {
                                                        null
                                                    }
                                                }.ifEmpty { emptyList() },
                                        )
                                    }.ifEmpty { emptyList() },
                            ),
                    )
                }.ifEmpty { emptyList() },
        )
    }

    override fun getCompetitionsByAthlete(athleteId: Int): List<CompetitionWithHeats> {
        val query =
            """
            select c.id, c.uid, c.date as competition_date, c.location, c.place, c.name, h.id as heat_id, h.score, 
            wa.activity as water_activity_id, a.uid as athlete_id, a.microcycle, wa.rpe, wa.condition, wa.trimp, 
            wa.duration, w.id as wave_id, w.points, w.right_side, w.wave_order, m.id as maneuver_id, 
            wm.id as water_maneuver_id, wm.name as water_maneuver_name, wm.url, m.success, m.maneuver_order
            from waveCoach.competition c
            left join waveCoach.heat h on c.id = h.competition
            left join waveCoach.water wa on h.water_activity = wa.activity
            left join waveCoach.activity a on wa.activity = a.id
            left join waveCoach.wave w on wa.activity = w.activity
            left join waveCoach.maneuver m on w.id = m.wave
            left join waveCoach.water_maneuver wm on m.maneuver = wm.id
            where c.uid = :athleteId
            order by c.date asc
            """.trimIndent()

        val rows =
            handle.createQuery(query)
                .bind("athleteId", athleteId)
                .mapTo<Row>()
                .list()

        return rows.groupBy { it.id }.map { (_, competitionRows) ->
            val competitionInfo = competitionRows.first()
            CompetitionWithHeats(
                id = competitionInfo.id,
                uid = competitionInfo.uid,
                date = competitionInfo.competitionDate,
                location = competitionInfo.location,
                place = competitionInfo.place,
                name = competitionInfo.name,
                heats =
                    competitionRows.groupBy { it.heatId }.filterKeys { it != null }.map { (_, heatRows) ->
                        val heatInfo = heatRows.first()
                        HeatWithWaterActivity(
                            id = heatInfo.heatId!!,
                            score = heatInfo.score ?: 0f,
                            waterActivity =
                                WaterActivityWithWaves(
                                    id = heatInfo.waterActivityId!!,
                                    athleteId = heatInfo.athleteId ?: 0,
                                    date = heatInfo.date ?: 0L,
                                    microcycleId = heatInfo.microcycle ?: 0,
                                    rpe = heatInfo.rpe ?: 0,
                                    condition = heatInfo.condition ?: "",
                                    trimp = heatInfo.trimp ?: 0,
                                    duration = heatInfo.duration ?: 0,
                                    waves =
                                        heatRows.groupBy { it.waveId }.filterKeys { it != null }.map { (_, waveRows) ->
                                            val waveInfo = waveRows.first()
                                            WaveWithManeuvers(
                                                id = waveInfo.waveId!!,
                                                points = waveInfo.points ?: 0f,
                                                rightSide = waveInfo.rightSide ?: false,
                                                order = waveInfo.waveOrder ?: 0,
                                                maneuvers =
                                                    waveRows.mapNotNull { maneuverRow ->
                                                        if (maneuverRow.maneuverId != null) {
                                                            Maneuver(
                                                                id = maneuverRow.maneuverId!!,
                                                                wave = waveInfo.waveId!!,
                                                                waterManeuverId =
                                                                    maneuverRow.waterManeuverId ?: 0,
                                                                waterManeuverName =
                                                                    maneuverRow.waterManeuverName ?: "",
                                                                url = maneuverRow.url ?: "",
                                                                success = maneuverRow.success ?: false,
                                                                maneuverOrder =
                                                                    maneuverRow.maneuverOrder ?: 0,
                                                            )
                                                        } else {
                                                            null
                                                        }
                                                    }.ifEmpty { emptyList() },
                                            )
                                        }.ifEmpty { emptyList() },
                                ),
                        )
                    }.ifEmpty { emptyList() },
            )
        }.ifEmpty { emptyList() }
    }

    override fun updateCompetition(
        id: Int,
        date: Long?,
        location: String?,
        place: Int?,
        name: String?,
    ) {
        handle.createUpdate(
            """
            update waveCoach.competition set
            date = coalesce(:date, date),
            location = coalesce(:location, location),
            place = coalesce(:place, place),
            name = coalesce(:name, name)
            where id = :id
            """.trimIndent(),
        )
            .bind("id", id)
            .bind("date", date)
            .bind("location", location)
            .bind("place", place)
            .bind("name", name)
            .execute()
    }

    override fun competitionExists(id: Int): Boolean {
        return handle.createQuery(
            """
            select exists(select 1 from waveCoach.competition where id = :id)
            """.trimIndent(),
        )
            .bind("id", id)
            .mapTo<Boolean>()
            .one()
    }

    override fun removeCompetition(id: Int) {
        handle.createUpdate(
            """
            delete from waveCoach.competition where id = :id
            """.trimIndent(),
        )
            .bind("id", id)
            .execute()
    }

    override fun storeHeats(heats: List<HeatToInsert>): List<Int> =
        handle.prepareBatch(
            """
            insert into waveCoach.heat (competition, water_activity, score)
            values (:competition, :water_activity, :score)
            """.trimIndent(),
        ).use { batch ->
            heats.forEach { heat ->
                batch.bind("competition", heat.competitionId)
                    .bind("water_activity", heat.waterActivityId)
                    .bind("score", heat.score)
                    .add()
            }
            batch.executeAndReturnGeneratedKeys()
                .mapTo<Int>()
                .list()
        }

    override fun getHeatsByCompetition(competitionId: Int): List<Heat> {
        return handle.createQuery(
            """
            select id, competition, water_activity, score
            from waveCoach.heat
            where competition = :competitionId
            """.trimIndent(),
        )
            .bind("competitionId", competitionId)
            .mapTo<Heat>()
            .list()
    }

    override fun updateHeats(heats: List<HeatToUpdate>) {
        handle.prepareBatch(
            """
            update waveCoach.heat
            set score = coalesce(:score, score)
            where id = :id
            """.trimIndent(),
        ).use { batch ->
            heats.forEach { heat ->
                batch.bind("id", heat.id)
                    .bind("score", heat.score)
                    .add()
            }
            batch.execute()
        }
    }

    override fun removeHeatsById(heatIds: List<Int>) {
        handle.prepareBatch(
            """
            delete from waveCoach.heat where id = :heatId
            """.trimIndent(),
        ).use { batch ->
            heatIds.forEach { heatId ->
                batch.bind("heatId", heatId).add()
            }
            batch.execute()
        }
    }
}

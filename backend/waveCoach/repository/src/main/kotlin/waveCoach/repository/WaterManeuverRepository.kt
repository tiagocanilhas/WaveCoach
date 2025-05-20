package waveCoach.repository

import waveCoach.domain.WaterManeuver

interface WaterManeuverRepository {

    fun storeWaterManeuver(name: String): Int

    fun getWaterManeuverByName(name: String): WaterManeuver?

    fun getWaterManeuverById(id: Int): WaterManeuver?

    fun getAllWaterManeuvers(): List<WaterManeuver>

    fun updateWaterManeuver(id: Int, name: String)

    fun removeWaterManeuver(id: Int)
}
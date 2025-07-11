package waveCoach.repository

import waveCoach.domain.WaterManeuver

interface WaterManeuverRepository {
    fun storeWaterManeuver(
        name: String,
        url: String?,
    ): Int

    fun isWaterManeuverValid(id: Int): Boolean

    fun getWaterManeuverByName(name: String): WaterManeuver?

    fun getWaterManeuverById(id: Int): WaterManeuver?

    fun getAllWaterManeuvers(): List<WaterManeuver>

    fun updateWaterManeuver(
        id: Int,
        name: String,
    )

    fun removeWaterManeuver(id: Int)
}

package waveCoach.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import waveCoach.domain.WaterManeuver
import waveCoach.repository.WaterManeuverRepository

class JdbiWaterManeuverRepository(
    private val handle: Handle,
) : WaterManeuverRepository {
    override fun storeWaterManeuver(
        name: String,
        url: String?,
    ): Int =
        handle.createUpdate("insert into waveCoach.water_maneuver (name, url) values (:name, :url)")
            .bind("name", name)
            .bind("url", url)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .one()

    override fun getWaterManeuverByName(name: String) =
        handle.createQuery("select * from waveCoach.water_maneuver where name = :name")
            .bind("name", name)
            .mapTo<WaterManeuver>()
            .singleOrNull()

    override fun getWaterManeuverById(id: Int) =
        handle.createQuery("select * from waveCoach.water_maneuver where id = :id")
            .bind("id", id)
            .mapTo<WaterManeuver>()
            .singleOrNull()

    override fun getAllWaterManeuvers(): List<WaterManeuver> =
        handle.createQuery("select * from waveCoach.water_maneuver")
            .mapTo<WaterManeuver>()
            .list()

    override fun updateWaterManeuver(
        id: Int,
        name: String,
    ) {
        TODO("Not yet implemented")
    }

    override fun removeWaterManeuver(id: Int) {
        TODO("Not yet implemented")
    }

}

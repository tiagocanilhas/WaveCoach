package waveCoach.services

import org.springframework.stereotype.Component
import waveCoach.domain.WaterManeuver
import waveCoach.domain.WaterManeuverDomain
import waveCoach.repository.TransactionManager
import waveCoach.utils.Either
import waveCoach.utils.failure
import waveCoach.utils.success

sealed class CreateWaterManeuverError {
    data object InvalidName : CreateWaterManeuverError()
    data object NameAlreadyExists : CreateWaterManeuverError()
}
typealias CreateWaterManeuverResult = Either<CreateWaterManeuverError, Int>

@Component
class WaterManeuverServices(
    private val transactionManager: TransactionManager,
    private val waterManeuverDomain: WaterManeuverDomain,
) {

    fun createWaterManeuver(name: String): CreateWaterManeuverResult {
        if (!waterManeuverDomain.isNameValid(name)) return failure(CreateWaterManeuverError.InvalidName)

        return transactionManager.run {
            val waterManeuverRepository = it.waterManeuverRepository

            if (waterManeuverRepository.getWaterManeuverByName(name) != null)
                return@run failure(CreateWaterManeuverError.NameAlreadyExists)

            success(waterManeuverRepository.storeWaterManeuver(name))
        }
    }

    fun getWaterManeuvers(): List<WaterManeuver> {
        return transactionManager.run {
            val waterManeuverRepository = it.waterManeuverRepository

            waterManeuverRepository.getAllWaterManeuvers()
        }
    }
}
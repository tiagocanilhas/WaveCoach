package waveCoach.services

import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import waveCoach.domain.WaterManeuver
import waveCoach.domain.WaterManeuverDomain
import waveCoach.repository.TransactionManager
import waveCoach.utils.Either
import waveCoach.utils.failure
import waveCoach.utils.success

sealed class CreateWaterManeuverError {
    data object InvalidName : CreateWaterManeuverError()

    data object NameAlreadyExists : CreateWaterManeuverError()

    data object InvalidPhoto : CreateWaterManeuverError()
}
typealias CreateWaterManeuverResult = Either<CreateWaterManeuverError, Int>

@Component
class WaterManeuverServices(
    private val transactionManager: TransactionManager,
    private val waterManeuverDomain: WaterManeuverDomain,
    private val cloudinaryServices: CloudinaryServices,
) {
    fun createWaterManeuver(
        name: String,
        photo: MultipartFile?,
    ): CreateWaterManeuverResult {
        if (!waterManeuverDomain.isNameValid(name)) return failure(CreateWaterManeuverError.InvalidName)

        return transactionManager.run {
            val waterManeuverRepository = it.waterManeuverRepository

            if (waterManeuverRepository.getWaterManeuverByName(name) != null) {
                return@run failure(CreateWaterManeuverError.NameAlreadyExists)
            }

            val url =
                photo?.let { file ->
                    cloudinaryServices.uploadManeuverImage(file)
                        ?: return@run failure(CreateWaterManeuverError.InvalidPhoto)
                }

            success(waterManeuverRepository.storeWaterManeuver(name, url))
        }
    }

    fun getWaterManeuvers(): List<WaterManeuver> {
        return transactionManager.run {
            val waterManeuverRepository = it.waterManeuverRepository

            waterManeuverRepository.getAllWaterManeuvers()
        }
    }
}

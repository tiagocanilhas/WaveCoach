package waveCoach.services

import org.springframework.stereotype.Component
import waveCoach.domain.GymExercise
import waveCoach.domain.GymExerciseDomain
import waveCoach.repository.TransactionManager
import waveCoach.utils.Either
import waveCoach.utils.failure
import waveCoach.utils.success

sealed class CreateGymExerciseError {
    data object InvalidCategory : CreateGymExerciseError()

    data object InvalidName : CreateGymExerciseError()

    data object NameAlreadyExists : CreateGymExerciseError()
}
typealias CreateGymExerciseResult = Either<CreateGymExerciseError, Int>

sealed class UpdateGymExerciseError {
    data object GymExerciseNotFound : UpdateGymExerciseError()

    data object InvalidCategory : UpdateGymExerciseError()

    data object InvalidName : UpdateGymExerciseError()

    data object NameAlreadyExists : UpdateGymExerciseError()
}
typealias UpdateGymExerciseResult = Either<UpdateGymExerciseError, Int>

sealed class RemoveGymExerciseError {
    data object GymExerciseNotFound : RemoveGymExerciseError()
}
typealias RemoveGymExerciseResult = Either<RemoveGymExerciseError, Int>

@Component
class GymExerciseServices(
    private val transactionManager: TransactionManager,
    private val gymExerciseDomain: GymExerciseDomain,
) {
    fun createGymExercise(
        name: String,
        category: String,
    ): CreateGymExerciseResult {
        if (!gymExerciseDomain.isCategoryValid(category)) return failure(CreateGymExerciseError.InvalidCategory)

        if (!gymExerciseDomain.isNameValid(name)) return failure(CreateGymExerciseError.InvalidName)

        return transactionManager.run {
            val gymActivityRepository = it.gymActivityRepository

            if (gymActivityRepository.getGymExerciseByName(name) != null)
                return@run failure(CreateGymExerciseError.NameAlreadyExists)


            val exerciseId = gymActivityRepository.storeGymExercise(name, category)

            success(exerciseId)
        }
    }

    fun getAllGymExercises(): List<GymExercise> {
        return transactionManager.run {
            val gymActivityRepository = it.gymActivityRepository

            gymActivityRepository.getAllGymExercises()
        }
    }

    fun updateGymExercise(
        id: Int,
        name: String,
        category: String,
    ): UpdateGymExerciseResult {
        if (!gymExerciseDomain.isCategoryValid(category)) return failure(UpdateGymExerciseError.InvalidCategory)

        if (!gymExerciseDomain.isNameValid(name)) return failure(UpdateGymExerciseError.InvalidName)

        return transactionManager.run {
            val gymActivityRepository = it.gymActivityRepository

            if (gymActivityRepository.getGymExerciseByName(name) != null)
                return@run failure(UpdateGymExerciseError.NameAlreadyExists)

            if (!gymActivityRepository.isGymExerciseValid(id))
                return@run failure(UpdateGymExerciseError.GymExerciseNotFound)

            gymActivityRepository.updateGymExercise(id, name, category)

            success(id)
        }
    }

    fun removeGymExercise(id: Int): RemoveGymExerciseResult {
        return transactionManager.run {
            val gymActivityRepository = it.gymActivityRepository

            if (!gymActivityRepository.isGymExerciseValid(id))
                return@run failure(RemoveGymExerciseError.GymExerciseNotFound)

            gymActivityRepository.removeGymExercise(id)

            success(id)
        }
    }
}
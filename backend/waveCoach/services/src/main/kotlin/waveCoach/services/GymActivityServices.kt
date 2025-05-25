package waveCoach.services

import org.springframework.stereotype.Component
import waveCoach.domain.GymActivityWithExercises
import waveCoach.domain.ExerciseWithSets
import waveCoach.domain.SetsDomain
import waveCoach.repository.TransactionManager
import waveCoach.utils.Either
import waveCoach.utils.failure
import waveCoach.utils.success

data class ExerciseInputInfo(
    val sets: List<SetInputInfo>,
    val gymExerciseId: Int,
)

data class SetInputInfo(
    val reps: Int,
    val weight: Float,
    val restTime: Float,
)

/*data class UpdateExerciseInputInfo(
    val id: Int?,
    val sets: List<UpdateSetInputInfo>?,
    val gymExerciseId: Int?,
    val exerciseOrder: Int?,
)

data class UpdateSetInputInfo(
    val id: Int?,
    val reps: Int?,
    val weight: Float?,
    val rest: Float?,
    val setOrder: Int?,
)*/

sealed class CreateGymActivityError {
    data object InvalidDate : CreateGymActivityError()

    data object AthleteNotFound : CreateGymActivityError()

    data object NotAthletesCoach : CreateGymActivityError()

    data object ActivityWithoutMicrocycle : CreateGymActivityError()

    data object InvalidGymExercise : CreateGymActivityError()

    data object InvalidSet : CreateGymActivityError()
}
typealias CreateGymActivityResult = Either<CreateGymActivityError, Int>

sealed class GetGymActivityError {
    data object ActivityNotFound : GetGymActivityError()

    data object NotAthletesCoach : GetGymActivityError()
}
typealias GetGymActivityResult = Either<GetGymActivityError, GymActivityWithExercises>

/*sealed class UpdateGymActivityError {
    data object InvalidDate : UpdateGymActivityError()

    data object InvalidGymExercise : UpdateGymActivityError()

    data object InvalidSet : UpdateGymActivityError()

    data object AthleteNotFound : UpdateGymActivityError()

    data object NotAthletesCoach : UpdateGymActivityError()

    data object NotAthletesActivity : UpdateGymActivityError()
}
typealias UpdateGymActivityResult = Either<UpdateGymActivityError, Int>*/

sealed class RemoveGymActivityError {
    data object NotAthletesCoach : RemoveGymActivityError()

    data object ActivityNotFound : RemoveGymActivityError()
}
typealias RemoveGymActivityResult = Either<RemoveGymActivityError, Int>

@Component
class GymActivityServices(
    private val transactionManager: TransactionManager,
    private val setsDomain: SetsDomain
) {
    fun createGymActivity(
        coachId: Int,
        uid: Int,
        date: String,
        exercises: List<ExerciseInputInfo>
    ): CreateGymActivityResult {
        val dateLong = dateToLong(date) ?: return failure(CreateGymActivityError.InvalidDate)

        return transactionManager.run {
            val athleteRepository = it.athleteRepository
            val activityRepository = it.activityRepository
            val gymActivityRepository = it.gymActivityRepository

            val athlete = athleteRepository.getAthlete(uid)
                ?: return@run failure(CreateGymActivityError.AthleteNotFound)

            if (athlete.coach != coachId) return@run failure(CreateGymActivityError.NotAthletesCoach)

            val micro = activityRepository.getMicrocycleByDate(dateLong, uid)
                ?: return@run failure(CreateGymActivityError.ActivityWithoutMicrocycle)

            val activityID = activityRepository.storeActivity(uid, dateLong, micro.id)
            gymActivityRepository.storeGymActivity(activityID)

            exercises.forEachIndexed { exerciseOrder, exercise ->
                if (!gymActivityRepository.isGymExerciseValid(exercise.gymExerciseId))
                    return@run failure(CreateGymActivityError.InvalidGymExercise)

                val exerciseId = gymActivityRepository.storeExercise(activityID, exercise.gymExerciseId, exerciseOrder)
                exercise.sets.forEachIndexed { setOrder, set ->
                    if (!setsDomain.checkSet(set.reps, set.weight, set.restTime))
                        return@run failure(CreateGymActivityError.InvalidSet)

                    gymActivityRepository.storeSet(exerciseId, set.reps, set.weight, set.restTime, setOrder)
                }
            }
            success(activityID)
        }
    }

    fun getGymActivity(
        uid: Int,
        activityId: Int,
    ): GetGymActivityResult {
        return transactionManager.run {
            val athleteRepository = it.athleteRepository
            val activityRepository = it.activityRepository
            val gymActivityRepository = it.gymActivityRepository

            val activity = activityRepository.getActivityById(activityId)
                ?: return@run failure(GetGymActivityError.ActivityNotFound)

            val athlete = athleteRepository.getAthlete(activity.uid)!!

            if (athlete.uid != uid && athlete.coach != uid)
                return@run failure(GetGymActivityError.NotAthletesCoach)

            val exercises = gymActivityRepository.getExercises(activityId)

            val exercisesWithSets = exercises.map { exercise ->
                val sets = gymActivityRepository.getSets(exercise.id)
                ExerciseWithSets(exercise.id, exercise.activity, exercise.name, exercise.exerciseOrder, exercise.url, sets)
            }

            success(GymActivityWithExercises(activity.id, activity.uid, activity.date, activity.type, exercisesWithSets))
        }
    }

    /*fun updateGymActivity(
        coachId: Int,
        uid: Int,
        activityId: Int,
        date: String?,
        exercises: List<UpdateExerciseInputInfo>?,
    ): UpdateGymActivityResult {
        return transactionManager.run {
            val athleteRepository = it.athleteRepository
            val activityRepository = it.activityRepository
            val gymActivityRepository = it.gymActivityRepository

            val athlete =
                athleteRepository.getAthlete(uid) ?: return@run failure(UpdateGymActivityError.AthleteNotFound)
            if (athlete.coach != coachId) return@run failure(UpdateGymActivityError.NotAthletesCoach)

            val activity =
                activityRepository.getActivityById(activityId)
                    ?: return@run failure(UpdateGymActivityError.ActivityNotFound)

            if (activity.uid != uid) return@run failure(UpdateGymActivityError.NotAthletesActivity)

            if (date != null) {
                val dateLong = dateToLong(date) ?: return@run failure(UpdateGymActivityError.InvalidDate)
                activityRepository.updateActivity(activityId, dateLong)
            }

            exercises?.forEach { exercise ->
                if (exercise.id == null) return@forEach

                if (exercise.exerciseOrder != null) {
                    gymActivityRepository.updateExerciseOrder(exercise.id, exercise.exerciseOrder)
                }

                if (exercise.gymExerciseId != null) {
                    if (!gymActivityRepository.isGymExerciseValid(exercise.gymExerciseId))
                        return@run failure(UpdateGymActivityError.InvalidGymExercise)

                    gymActivityRepository.updateExercise(exercise.id, exercise.gymExerciseId)
                }
                if (exercise.sets != null) {
                    exercise.sets.forEach { set ->
                        if (set.id == null) return@forEach

                        if (set.setOrder != null) {
                            gymActivityRepository.updateSetOrder(set.id, set.setOrder)
                        }

                        if (!checkSet(set)) return@run failure(UpdateGymActivityError.InvalidSet)

                        gymActivityRepository.updateSet(
                            set.id,
                            set.reps,
                            set.weight,
                            set.rest,
                            set.setOrder,
                        )
                    }
                }
            }

            success(activity.id)
        }
    }*/

    fun removeGymActivity(
        coachId: Int,
        activityId: Int,
    ): RemoveGymActivityResult {
        return transactionManager.run {
            val athleteRepository = it.athleteRepository
            val activityRepository = it.activityRepository
            val gymActivityRepository = it.gymActivityRepository

            val activity =
                activityRepository.getActivityById(activityId)
                    ?: return@run failure(RemoveGymActivityError.ActivityNotFound)

            val athlete =
                athleteRepository.getAthlete(activity.uid)

            if (athlete!!.coach != coachId) return@run failure(RemoveGymActivityError.NotAthletesCoach)

            gymActivityRepository.removeSetsByActivity(activityId)
            gymActivityRepository.removeExercisesByActivity(activityId)
            gymActivityRepository.removeGymActivity(activityId)
            activityRepository.removeActivity(activityId)

            success(activityId)
        }
    }
}
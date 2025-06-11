package waveCoach.services

import org.springframework.stereotype.Component
import waveCoach.domain.ExerciseWithSets
import waveCoach.domain.GymActivityWithExercises
import waveCoach.domain.SetToInsert
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

    data object NotGymActivity : GetGymActivityError()
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

    data object NotGymActivity : RemoveGymActivityError()
}
typealias RemoveGymActivityResult = Either<RemoveGymActivityError, Int>

sealed class AddExerciseError {
    data object InvalidGymExercise : AddExerciseError()

    data object ActivityNotFound : AddExerciseError()

    data object NotAthletesCoach : AddExerciseError()

    data object InvalidSet : AddExerciseError()

    data object NotGymActivity : AddExerciseError()

    data object InvalidOrder : AddExerciseError()
}
typealias AddExerciseResult = Either<AddExerciseError, Int>

sealed class RemoveExerciseError {
    data object ActivityNotFound : RemoveExerciseError()

    data object NotAthletesCoach : RemoveExerciseError()

    data object NotGymActivity : RemoveExerciseError()

    data object ExerciseNotFound : RemoveExerciseError()

    data object NotActivityExercise : RemoveExerciseError()
}
typealias RemoveExerciseResult = Either<RemoveExerciseError, Int>

sealed class AddSetError {
    data object ActivityNotFound : AddSetError()

    data object NotAthletesCoach : AddSetError()

    data object InvalidSet : AddSetError()

    data object NotGymActivity : AddSetError()

    data object ExerciseNotFound : AddSetError()

    data object NotActivityExercise : AddSetError()

    data object InvalidOrder : AddSetError()
}
typealias AddSetResult = Either<AddSetError, Int>

sealed class RemoveSetError {
    data object ActivityNotFound : RemoveSetError()

    data object NotAthletesCoach : RemoveSetError()

    data object NotGymActivity : RemoveSetError()

    data object ExerciseNotFound : RemoveSetError()

    data object NotActivityExercise : RemoveSetError()

    data object NotExerciseSet : RemoveSetError()

    data object SetNotFound : RemoveSetError()
}
typealias RemoveSetResult = Either<RemoveSetError, Int>

@Component
class GymActivityServices(
    private val transactionManager: TransactionManager,
    private val setsDomain: SetsDomain,
) {
    fun createGymActivity(
        coachId: Int,
        uid: Int,
        date: String,
        exercises: List<ExerciseInputInfo>,
    ): CreateGymActivityResult {
        val dateLong = dateToLong(date) ?: return failure(CreateGymActivityError.InvalidDate)

        return transactionManager.run {
            val athleteRepository = it.athleteRepository
            val activityRepository = it.activityRepository
            val gymActivityRepository = it.gymActivityRepository

            val athlete =
                athleteRepository.getAthlete(uid)
                    ?: return@run failure(CreateGymActivityError.AthleteNotFound)

            if (athlete.coach != coachId) return@run failure(CreateGymActivityError.NotAthletesCoach)

            val micro =
                activityRepository.getMicrocycleByDate(dateLong, uid)
                    ?: return@run failure(CreateGymActivityError.ActivityWithoutMicrocycle)

            val activityID = activityRepository.storeActivity(uid, dateLong, micro.id)
            gymActivityRepository.storeGymActivity(activityID)

            exercises.forEachIndexed { exerciseOrder, exercise ->
                if (!gymActivityRepository.isGymExerciseValid(exercise.gymExerciseId)) {
                    return@run failure(CreateGymActivityError.InvalidGymExercise)
                }

                val exerciseId = gymActivityRepository.storeExercise(activityID, exercise.gymExerciseId, exerciseOrder)
                exercise.sets.forEachIndexed { setOrder, set ->
                    if (!setsDomain.checkSet(set.reps, set.weight, set.restTime)) {
                        return@run failure(CreateGymActivityError.InvalidSet)
                    }

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

            val activity =
                activityRepository.getActivityById(activityId)
                    ?: return@run failure(GetGymActivityError.ActivityNotFound)

            if (activity.type != waveCoach.domain.ActivityType.GYM) {
                return@run failure(GetGymActivityError.NotGymActivity)
            }

            val athlete = athleteRepository.getAthlete(activity.uid)!!

            if (athlete.uid != uid && athlete.coach != uid) {
                return@run failure(GetGymActivityError.NotAthletesCoach)
            }

            val exercises = gymActivityRepository.getExercises(activityId)

            val exercisesWithSets =
                exercises.map { exercise ->
                    val sets = gymActivityRepository.getSets(exercise.id)
                    ExerciseWithSets(
                        exercise.id,
                        exercise.activity,
                        exercise.name,
                        exercise.exerciseOrder,
                        exercise.url,
                        sets,
                    )
                }

            success(
                GymActivityWithExercises(
                    activity.id,
                    activity.uid,
                    activity.date,
                    activity.type,
                    exercisesWithSets,
                ),
            )
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

            val activity =
                activityRepository.getActivityById(activityId)
                    ?: return@run failure(RemoveGymActivityError.ActivityNotFound)

            if (activity.type != waveCoach.domain.ActivityType.GYM) {
                return@run failure(RemoveGymActivityError.NotGymActivity)
            }

            val athlete = athleteRepository.getAthlete(activity.uid)!!

            if (athlete.coach != coachId) return@run failure(RemoveGymActivityError.NotAthletesCoach)

            activityRepository.removeActivity(activityId)

            success(activityId)
        }
    }

    fun addExercise(
        coachId: Int,
        activityId: Int,
        gymExerciseId: Int,
        sets: List<SetInputInfo>,
        order: Int,
    ): AddExerciseResult {
        return transactionManager.run { it ->
            val athleteRepository = it.athleteRepository
            val activityRepository = it.activityRepository
            val gymActivityRepository = it.gymActivityRepository

            val activity =
                activityRepository.getActivityById(activityId)
                    ?: return@run failure(AddExerciseError.ActivityNotFound)

            if (activity.type != waveCoach.domain.ActivityType.GYM) {
                return@run failure(AddExerciseError.NotGymActivity)
            }

            val athlete = athleteRepository.getAthlete(activity.uid)!!

            if (athlete.coach != coachId) return@run failure(AddExerciseError.NotAthletesCoach)

            if (!gymActivityRepository.isGymExerciseValid(gymExerciseId)) {
                return@run failure(AddExerciseError.InvalidGymExercise)
            }

            if (order <= 0 || gymActivityRepository.verifyExerciseOrder(activityId, order))
                return@run failure(AddExerciseError.InvalidOrder)

            val exerciseId = gymActivityRepository.storeExercise(activityId, gymExerciseId, order)

            val setsToInsert = sets.mapIndexed { setOrder, set ->
                if (!setsDomain.checkSet(set.reps, set.weight, set.restTime)) {
                    return@run failure(AddExerciseError.InvalidSet)
                }

                SetToInsert(
                    exerciseId,
                    set.reps,
                    set.weight,
                    set.restTime,
                    setOrder,
                )
            }

            gymActivityRepository.storeSets(setsToInsert)

            success(exerciseId)
        }
    }

    fun removeExercise(
        coachId: Int,
        activityId: Int,
        exerciseId: Int,
    ): RemoveExerciseResult {
        return transactionManager.run {
            val athleteRepository = it.athleteRepository
            val activityRepository = it.activityRepository
            val gymActivityRepository = it.gymActivityRepository

            val activity =
                activityRepository.getActivityById(activityId)
                    ?: return@run failure(RemoveExerciseError.ActivityNotFound)

            if (activity.type != waveCoach.domain.ActivityType.GYM)
                return@run failure(RemoveExerciseError.NotGymActivity)


            val exercise = gymActivityRepository.getExerciseById(exerciseId)
                ?: return@run failure(RemoveExerciseError.ExerciseNotFound)

            if (exercise.activity != activityId)
                return@run failure(RemoveExerciseError.NotActivityExercise)

            val athlete = athleteRepository.getAthlete(activity.uid)!!

            if (athlete.coach != coachId) return@run failure(RemoveExerciseError.NotAthletesCoach)

            gymActivityRepository.removeExerciseById(exerciseId)

            success(exerciseId)
        }
    }

    fun addSet(
        coachId: Int,
        activityId: Int,
        exerciseId: Int,
        reps: Int,
        weight: Float,
        restTime: Float,
        order: Int,
    ): AddSetResult {
        return transactionManager.run { it ->
            val athleteRepository = it.athleteRepository
            val activityRepository = it.activityRepository
            val gymActivityRepository = it.gymActivityRepository

            val activity =
                activityRepository.getActivityById(activityId)
                    ?: return@run failure(AddSetError.ActivityNotFound)

            if (activity.type != waveCoach.domain.ActivityType.GYM)
                return@run failure(AddSetError.NotGymActivity)

            val exercise = gymActivityRepository.getExerciseById(exerciseId)
                ?: return@run failure(AddSetError.ExerciseNotFound)

            if (exercise.activity != activityId)
                return@run failure(AddSetError.NotActivityExercise)

            val athlete = athleteRepository.getAthlete(activity.uid)!!

            if (athlete.coach != coachId) return@run failure(AddSetError.NotAthletesCoach)

            if (!setsDomain.checkSet(reps, weight, restTime))
                return@run failure(AddSetError.InvalidSet)

            if (order <= 0 || gymActivityRepository.verifySetOrder(exerciseId, order))
                return@run failure(AddSetError.InvalidOrder)

            val setId = gymActivityRepository.storeSet(exerciseId, reps, weight, restTime, order)

            success(setId)
        }
    }

    fun removeSet(
        coachId: Int,
        activityId: Int,
        exerciseId: Int,
        setId: Int,
    ): RemoveSetResult {
        return transactionManager.run { it ->
            val athleteRepository = it.athleteRepository
            val activityRepository = it.activityRepository
            val gymActivityRepository = it.gymActivityRepository

            val activity =
                activityRepository.getActivityById(activityId)
                    ?: return@run failure(RemoveSetError.ActivityNotFound)

            if (activity.type != waveCoach.domain.ActivityType.GYM)
                return@run failure(RemoveSetError.NotGymActivity)

            val exercise = gymActivityRepository.getExerciseById(exerciseId)
                ?: return@run failure(RemoveSetError.ExerciseNotFound)

            if (exercise.activity != activityId)
                return@run failure(RemoveSetError.NotActivityExercise)

            val athlete = athleteRepository.getAthlete(activity.uid)!!

            if (athlete.coach != coachId) return@run failure(RemoveSetError.NotAthletesCoach)

            val set = gymActivityRepository.getSetById(setId)
                ?: return@run failure(RemoveSetError.SetNotFound)

            if (set.exerciseId != exerciseId) return@run failure(RemoveSetError.NotExerciseSet)

            gymActivityRepository.removeSetById(setId)

            success(setId)
        }
    }
}

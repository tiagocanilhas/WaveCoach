package waveCoach.services

import org.springframework.stereotype.Component
import waveCoach.domain.ActivityType
import waveCoach.domain.ExerciseToInsert
import waveCoach.domain.ExerciseToUpdate
import waveCoach.domain.ExerciseWithSets
import waveCoach.domain.GymActivityWithExercises
import waveCoach.domain.SetToInsert
import waveCoach.domain.SetToUpdate
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

data class UpdateExerciseInputInfo(
    val id: Int?,
    val gymExerciseId: Int?,
    val sets: List<UpdateSetInputInfo>?,
    val order: Int?,
)

data class UpdateSetInputInfo(
    val id: Int?,
    val reps: Int?,
    val weight: Float?,
    val restTime: Float?,
    val order: Int?,
)

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

sealed class UpdateGymActivityError {
    data object InvalidDate : UpdateGymActivityError()

    data object InvalidGymExercise : UpdateGymActivityError()

    data object ExerciseNotFound : UpdateGymActivityError()

    data object InvalidExerciseOrder : UpdateGymActivityError()

    data object InvalidSet : UpdateGymActivityError()

    data object SetNotFound : UpdateGymActivityError()

    data object ActivityNotFound : UpdateGymActivityError()

    data object NotGymActivity : UpdateGymActivityError()

    data object NotAthletesCoach : UpdateGymActivityError()

    data object InvalidSetOrder : UpdateGymActivityError()

    data object InvalidSets : UpdateGymActivityError()
}
typealias UpdateGymActivityResult = Either<UpdateGymActivityError, Int>

sealed class RemoveGymActivityError {
    data object NotAthletesCoach : RemoveGymActivityError()

    data object ActivityNotFound : RemoveGymActivityError()

    data object NotGymActivity : RemoveGymActivityError()
}
typealias RemoveGymActivityResult = Either<RemoveGymActivityError, Int>

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

            val exercisesToInsert =
                exercises.mapIndexed { exerciseOrder, exercise ->
                    if (!gymActivityRepository.isGymExerciseValid(exercise.gymExerciseId)) {
                        return@run failure(CreateGymActivityError.InvalidGymExercise)
                    }

                    ExerciseToInsert(
                        activityID,
                        exercise.gymExerciseId,
                        exerciseOrder + 1,
                    )
                }

            val exercisesIds = gymActivityRepository.storeExercises(exercisesToInsert)

            val setsToInsert =
                exercises.flatMapIndexed { exerciseIndex, exercise ->
                    exercise.sets.mapIndexed { setOrder, set ->
                        if (!setsDomain.checkSet(set.reps, set.weight, set.restTime)) {
                            return@run failure(CreateGymActivityError.InvalidSet)
                        }

                        SetToInsert(
                            exercisesIds[exerciseIndex],
                            set.reps,
                            set.weight,
                            set.restTime,
                            setOrder + 1,
                        )
                    }
                }

            gymActivityRepository.storeSets(setsToInsert)

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

            if (activity.type != ActivityType.GYM) {
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

    fun updateGymActivity(
        uid: Int,
        activityId: Int,
        date: String?,
        exercises: List<UpdateExerciseInputInfo>?,
    ): UpdateGymActivityResult {
        val dateLong = date?.let { dateToLong(it) ?: return failure(UpdateGymActivityError.InvalidDate) }

        return transactionManager.run { it ->
            val athleteRepository = it.athleteRepository
            val activityRepository = it.activityRepository
            val gymActivityRepository = it.gymActivityRepository

            val activity =
                activityRepository.getActivityById(activityId)
                    ?: return@run failure(UpdateGymActivityError.ActivityNotFound)

            if (activity.type != ActivityType.GYM) {
                return@run failure(UpdateGymActivityError.NotGymActivity)
            }

            val athlete = athleteRepository.getAthlete(activity.uid)!!

            if (athlete.coach != uid) {
                return@run failure(UpdateGymActivityError.NotAthletesCoach)
            }

            if (dateLong != null && dateLong != activity.date) {
                activityRepository.updateActivity(activityId, dateLong)
            }

            if (exercises != null) {
                val (create, update, delete) = separateCreateUpdateDelete(exercises)

                val exercisesOnDB = gymActivityRepository.getExercises(activityId)

                // Update existing exercises
                val exercisesToUpdate =
                    update.map { exercise ->
                        if (exercise.order != null &&
                            (
                                exercise.order <= 0 ||
                                    !checkOrderConflict(
                                        exercisesOnDB, exercises, "exerciseOrder", exercise.order,
                                    )
                            )
                        ) {
                            return@run failure(UpdateGymActivityError.InvalidExerciseOrder)
                        }

                        if (exercisesOnDB.none { it.id == exercise.id }) {
                            return@run failure(UpdateGymActivityError.ExerciseNotFound)
                        }

                        ExerciseToUpdate(
                            exercise.id!!,
                            exercise.order,
                        )
                    }

                val setsCreate = mutableListOf<SetToInsert>()
                val setsUpdate = mutableListOf<SetToUpdate>()
                val setsDelete = mutableListOf<Int>()

                update.forEachIndexed { exerciseIndex, exercise ->
                    if (exercise.sets != null) {
                        val (setCreate, setUpdate, setDelete) = separateCreateUpdateDelete(exercise.sets)

                        val setsOnDB = gymActivityRepository.getSets(update[exerciseIndex].id!!)

                        // Update existing Sets
                        setUpdate.forEach {
                            if (!setsDomain.checkSet(it.reps, it.weight, it.restTime)) {
                                return@run failure(UpdateGymActivityError.InvalidSet)
                            }

                            if (setsOnDB.none { set -> set.id == it.id }) {
                                return@run failure(UpdateGymActivityError.SetNotFound)
                            }

                            if (it.order != null && (
                                    it.order <= 0 ||
                                        !checkOrderConflict(
                                            setsOnDB, exercise.sets, "setOrder", it.order,
                                        )
                                )
                            ) {
                                return@run failure(UpdateGymActivityError.InvalidSetOrder)
                            }

                            setsUpdate.add(
                                SetToUpdate(
                                    it.id!!,
                                    it.reps,
                                    it.weight,
                                    it.restTime,
                                    it.order,
                                ),
                            )
                        }
                        gymActivityRepository.updateSets(setsUpdate)

                        // Add New Sets
                        setCreate.forEach { set ->
                            if (!setsDomain.checkSet(set.reps!!, set.weight!!, set.restTime!!)) {
                                return@run failure(UpdateGymActivityError.InvalidSet)
                            }

                            if (set.order == null || set.order <= 0) {
                                return@run failure(UpdateGymActivityError.InvalidSetOrder)
                            }

                            setsCreate.add(
                                SetToInsert(
                                    update[exerciseIndex].id!!,
                                    set.reps,
                                    set.weight,
                                    set.restTime,
                                    set.order,
                                ),
                            )
                        }

                        // Delete Sets
                        if (setDelete.any { id -> setsOnDB.none { it.id == id } }) {
                            return@run failure(UpdateGymActivityError.SetNotFound)
                        }

                        setsDelete.addAll(setDelete)
                    }
                    gymActivityRepository.updateExercises(exercisesToUpdate)

                    gymActivityRepository.removeSetsById(setsDelete)
                }

                // Add New exercises
                val exercisesToInsert =
                    create.map { exercise ->
                        if (exercise.gymExerciseId == null || !gymActivityRepository.isGymExerciseValid(exercise.gymExerciseId)) {
                            return@run failure(UpdateGymActivityError.InvalidGymExercise)
                        }

                        if (exercise.order == null || exercise.order <= 0 ||
                            gymActivityRepository.verifyExerciseOrder(activityId, exercise.order)
                        ) {
                            return@run failure(UpdateGymActivityError.InvalidExerciseOrder)
                        }

                        ExerciseToInsert(
                            activityId,
                            exercise.gymExerciseId,
                            exercise.order,
                        )
                    }

                val exercisesCreatedIds = gymActivityRepository.storeExercises(exercisesToInsert)

                val setsToInsert =
                    create.flatMapIndexed { exerciseIndex, exercise ->
                        if (exercise.sets == null) return@run failure(UpdateGymActivityError.InvalidSets)

                        exercise.sets.mapIndexed { setOrder, set ->
                            if (!setsDomain.checkSet(set.reps, set.weight, set.restTime)) {
                                return@run failure(UpdateGymActivityError.InvalidSet)
                            }

                            if (set.reps == null || set.weight == null || set.restTime == null) {
                                return@run failure(UpdateGymActivityError.InvalidSet)
                            }

                            SetToInsert(
                                exercisesCreatedIds[exerciseIndex],
                                set.reps,
                                set.weight,
                                set.restTime,
                                setOrder,
                            )
                        }
                    }
                gymActivityRepository.storeSets(setsToInsert + setsCreate)

                // Delete exercises
                if (delete.any { id -> exercisesOnDB.none { it.id == id } }) {
                    return@run failure(UpdateGymActivityError.ExerciseNotFound)
                }
                gymActivityRepository.removeExercisesById(delete)
            }

            success(activityId)
        }
    }

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
}

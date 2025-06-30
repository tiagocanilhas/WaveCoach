package waveCoach.http

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import waveCoach.domain.AuthenticatedCoach
import waveCoach.domain.AuthenticatedUser
import waveCoach.http.model.input.AddExerciseInputModel
import waveCoach.http.model.input.AddSetInputModel
import waveCoach.http.model.input.CreateGymActivityInputModel
import waveCoach.http.model.input.UpdateGymActivityInputModel
import waveCoach.http.model.output.ExerciseWithSetsOutputModel
import waveCoach.http.model.output.GymActivityWithExercisesOutputModel
import waveCoach.http.model.output.Problem
import waveCoach.http.model.output.SetOutputModel
import waveCoach.services.*
import waveCoach.utils.Failure
import waveCoach.utils.Success

@RestController
class GymActivityController(
    private val gymActivityServices: GymActivityServices,
) {
    @PostMapping(Uris.GymActivity.CREATE)
    fun create(
        coach: AuthenticatedCoach,
        @RequestBody input: CreateGymActivityInputModel,
    ): ResponseEntity<*> {
        val result =
            gymActivityServices.createGymActivity(
                coach.info.id,
                input.athleteId,
                input.date,
                input.exercises.map { inputModel ->
                    ExerciseInputInfo(
                        inputModel.sets.map { SetInputInfo(it.reps, it.weight, it.restTime) },
                        inputModel.gymExerciseId,
                    )
                },
            )

        return when (result) {
            is Success ->
                ResponseEntity
                    .status(201)
                    .header("Location", Uris.GymActivity.byId(result.value).toASCIIString())
                    .build<Unit>()

            is Failure ->
                when (result.value) {
                    CreateGymActivityError.AthleteNotFound -> Problem.response(404, Problem.athleteNotFound)
                    CreateGymActivityError.InvalidDate -> Problem.response(400, Problem.invalidDate)
                    CreateGymActivityError.NotAthletesCoach -> Problem.response(403, Problem.notAthletesCoach)
                    CreateGymActivityError.ActivityWithoutMicrocycle ->
                        Problem.response(400, Problem.activityWithoutMicrocycle)

                    CreateGymActivityError.InvalidGymExercise -> Problem.response(400, Problem.invalidGymExercise)
                    CreateGymActivityError.InvalidSet -> Problem.response(400, Problem.invalidSet)
                }
        }
    }

    @GetMapping(Uris.GymActivity.GET_BY_ID)
    fun getById(
        user: AuthenticatedUser,
        @PathVariable activityId: String,
    ): ResponseEntity<*> {
        val activityIdInt = activityId.toIntOrNull() ?: return Problem.response(400, Problem.invalidGymActivityId)
        val result = gymActivityServices.getGymActivity(user.info.id, activityIdInt)

        return when (result) {
            is Success ->
                ResponseEntity
                    .status(200)
                    .body(
                        GymActivityWithExercisesOutputModel(
                            result.value.id,
                            result.value.date,
                            result.value.exercises.map { exercise ->
                                ExerciseWithSetsOutputModel(
                                    exercise.id,
                                    exercise.gymExercise,
                                    exercise.exerciseOrder,
                                    exercise.url,
                                    exercise.sets.map { set ->
                                        SetOutputModel(
                                            set.id,
                                            set.reps,
                                            set.weight,
                                            set.restTime,
                                            set.setOrder,
                                        )
                                    },
                                )
                            },
                        ),
                    )

            is Failure ->
                when (result.value) {
                    GetGymActivityError.ActivityNotFound -> Problem.response(404, Problem.gymActivityNotFound)
                    GetGymActivityError.NotAthletesCoach -> Problem.response(403, Problem.notAthletesCoach)
                    GetGymActivityError.NotGymActivity -> Problem.response(400, Problem.notGymActivity)
                }
        }
    }

    @PatchMapping(Uris.GymActivity.UPDATE)
    fun updateGymActivity(
        coach: AuthenticatedCoach,
        @PathVariable activityId: String,
        @RequestBody input: UpdateGymActivityInputModel,
    ): ResponseEntity<*> {
        val activityIdInt = activityId.toIntOrNull() ?: return Problem.response(400, Problem.invalidGymActivityId)

        val exercises = input.exercises?.map { exercise ->
            UpdateExerciseInputInfo(
                exercise.id,
                exercise.gymExerciseId,
                exercise.sets?.map { set ->
                    UpdateSetInputInfo(set.id, set.reps, set.weight, set.restTime, set.order)
                },
                exercise.order,
            )
        }

        val result = gymActivityServices.updateGymActivity(coach.info.id, activityIdInt, input.date, exercises)

        return when (result) {
            is Success -> ResponseEntity.status(204).build<Unit>()

            is Failure ->
                when (result.value) {
                    UpdateGymActivityError.InvalidDate -> Problem.response(400, Problem.invalidDate)
                    UpdateGymActivityError.InvalidGymExercise -> Problem.response(400, Problem.invalidGymExercise)
                    UpdateGymActivityError.ExerciseNotFound -> Problem.response(404, Problem.exerciseNotFound)
                    UpdateGymActivityError.InvalidExerciseOrder -> Problem.response(400, Problem.invalidOrder)
                    UpdateGymActivityError.SetNotFound -> Problem.response(404, Problem.setNotFound)
                    UpdateGymActivityError.InvalidSet -> Problem.response(400, Problem.invalidSet)
                    UpdateGymActivityError.ActivityNotFound -> Problem.response(404, Problem.gymActivityNotFound)
                    UpdateGymActivityError.NotGymActivity -> Problem.response(400, Problem.notGymActivity)
                    UpdateGymActivityError.NotAthletesCoach -> Problem.response(403, Problem.notAthletesCoach)
                }
        }
    }

    @DeleteMapping(Uris.GymActivity.REMOVE)
    fun remove(
        coach: AuthenticatedCoach,
        @PathVariable activityId: String,
    ): ResponseEntity<*> {
        val activityIdInt = activityId.toIntOrNull() ?: return Problem.response(400, Problem.invalidGymActivityId)
        val result = gymActivityServices.removeGymActivity(coach.info.id, activityIdInt)

        return when (result) {
            is Success -> ResponseEntity.status(204).build<Unit>()

            is Failure ->
                when (result.value) {
                    RemoveGymActivityError.ActivityNotFound -> Problem.response(404, Problem.gymActivityNotFound)
                    RemoveGymActivityError.NotAthletesCoach -> Problem.response(403, Problem.notAthletesCoach)
                    RemoveGymActivityError.NotGymActivity -> Problem.response(400, Problem.notGymActivity)
                }
        }
    }

    @PostMapping(Uris.GymActivity.ADD_EXERCISE)
    fun addExercise(
        coach: AuthenticatedCoach,
        @PathVariable activityId: String,
        @RequestBody input: AddExerciseInputModel,
    ): ResponseEntity<*> {
        val activityIdInt = activityId.toIntOrNull() ?: return Problem.response(400, Problem.invalidGymActivityId)
        val result = gymActivityServices.addExercise(
            coach.info.id,
            activityIdInt,
            input.gymExerciseId,
            input.sets.map { SetInputInfo(it.reps, it.weight, it.restTime) },
            input.order,
        )

        return when (result) {
            is Success ->
                ResponseEntity
                    .status(201)
                    .header("Location", Uris.GymActivity.exerciseById(activityIdInt, result.value).toASCIIString())
                    .build<Unit>()

            is Failure ->
                when (result.value) {
                    AddExerciseError.ActivityNotFound -> Problem.response(404, Problem.gymActivityNotFound)
                    AddExerciseError.NotAthletesCoach -> Problem.response(403, Problem.notAthletesCoach)
                    AddExerciseError.InvalidGymExercise -> Problem.response(400, Problem.invalidGymExercise)
                    AddExerciseError.InvalidSet -> Problem.response(400, Problem.invalidSet)
                    AddExerciseError.NotGymActivity -> Problem.response(400, Problem.notGymActivity)
                    AddExerciseError.InvalidOrder -> Problem.response(400, Problem.invalidOrder)
                }
        }
    }

    @DeleteMapping(Uris.GymActivity.REMOVE_EXERCISE)
    fun removeExercise(
        coach: AuthenticatedCoach,
        @PathVariable activityId: String,
        @PathVariable exerciseId: String,
    ): ResponseEntity<*> {
        val activityIdInt = activityId.toIntOrNull() ?: return Problem.response(400, Problem.invalidGymActivityId)
        val exerciseIdInt = exerciseId.toIntOrNull() ?: return Problem.response(400, Problem.invalidExerciseId)
        val result = gymActivityServices.removeExercise(coach.info.id, activityIdInt, exerciseIdInt)

        return when (result) {
            is Success -> ResponseEntity.status(204).build<Unit>()

            is Failure ->
                when (result.value) {
                    RemoveExerciseError.ActivityNotFound -> Problem.response(404, Problem.gymActivityNotFound)
                    RemoveExerciseError.NotAthletesCoach -> Problem.response(403, Problem.notAthletesCoach)
                    RemoveExerciseError.NotGymActivity -> Problem.response(400, Problem.notGymActivity)
                    RemoveExerciseError.ExerciseNotFound -> Problem.response(404, Problem.gymExerciseNotFound)
                    RemoveExerciseError.NotActivityExercise -> Problem.response(400, Problem.notActivityExercise)
                }
        }
    }

    @PostMapping(Uris.GymActivity.ADD_SET)
    fun addSet(
        coach: AuthenticatedCoach,
        @PathVariable activityId: String,
        @PathVariable exerciseId: String,
        @RequestBody input: AddSetInputModel,
    ): ResponseEntity<*> {
        val activityIdInt = activityId.toIntOrNull() ?: return Problem.response(400, Problem.invalidGymActivityId)
        val exerciseIdInt = exerciseId.toIntOrNull() ?: return Problem.response(400, Problem.invalidExerciseId)
        val result = gymActivityServices.addSet(
            coach.info.id,
            activityIdInt,
            exerciseIdInt,
            input.reps,
            input.weight,
            input.restTime,
            input.order,
        )

        return when (result) {
            is Success ->
                ResponseEntity
                    .status(201)
                    .header(
                        "Location",
                        Uris.GymActivity.setById(activityIdInt, exerciseIdInt, result.value).toASCIIString()
                    )
                    .build<Unit>()

            is Failure ->
                when (result.value) {
                    AddSetError.ActivityNotFound -> Problem.response(404, Problem.gymActivityNotFound)
                    AddSetError.NotAthletesCoach -> Problem.response(403, Problem.notAthletesCoach)
                    AddSetError.InvalidSet -> Problem.response(400, Problem.invalidSet)
                    AddSetError.NotGymActivity -> Problem.response(400, Problem.notGymActivity)
                    AddSetError.ExerciseNotFound -> Problem.response(404, Problem.exerciseNotFound)
                    AddSetError.NotActivityExercise -> Problem.response(400, Problem.notActivityExercise)
                    AddSetError.InvalidOrder -> Problem.response(400, Problem.invalidOrder)
                }
        }
    }

    @DeleteMapping(Uris.GymActivity.REMOVE_SET)
    fun removeSet(
        coach: AuthenticatedCoach,
        @PathVariable activityId: String,
        @PathVariable exerciseId: String,
        @PathVariable setId: String,
    ): ResponseEntity<*> {
        val activityIdInt = activityId.toIntOrNull() ?: return Problem.response(400, Problem.invalidGymActivityId)
        val exerciseIdInt = exerciseId.toIntOrNull() ?: return Problem.response(400, Problem.invalidExerciseId)
        val setIdInt = setId.toIntOrNull() ?: return Problem.response(400, Problem.invalidSetId)
        val result = gymActivityServices.removeSet(coach.info.id, activityIdInt, exerciseIdInt, setIdInt)

        return when (result) {
            is Success -> ResponseEntity.status(204).build<Unit>()

            is Failure ->
                when (result.value) {
                    RemoveSetError.ActivityNotFound -> Problem.response(404, Problem.gymActivityNotFound)
                    RemoveSetError.NotAthletesCoach -> Problem.response(403, Problem.notAthletesCoach)
                    RemoveSetError.NotGymActivity -> Problem.response(400, Problem.notGymActivity)
                    RemoveSetError.ExerciseNotFound -> Problem.response(404, Problem.exerciseNotFound)
                    RemoveSetError.NotActivityExercise -> Problem.response(400, Problem.notActivityExercise)
                    RemoveSetError.NotExerciseSet -> Problem.response(400, Problem.notExerciseSet)
                    RemoveSetError.SetNotFound -> Problem.response(404, Problem.setNotFound)
                }
        }
    }
}

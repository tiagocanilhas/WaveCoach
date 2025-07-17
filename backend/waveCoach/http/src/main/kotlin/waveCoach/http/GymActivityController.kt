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
                                    exercise.name,
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

        val exercises =
            input.exercises?.map { exercise ->
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
                    UpdateGymActivityError.InvalidExerciseOrder -> Problem.response(400, Problem.invalidExerciseOrder)
                    UpdateGymActivityError.SetNotFound -> Problem.response(404, Problem.setNotFound)
                    UpdateGymActivityError.InvalidSet -> Problem.response(400, Problem.invalidSet)
                    UpdateGymActivityError.ActivityNotFound -> Problem.response(404, Problem.gymActivityNotFound)
                    UpdateGymActivityError.NotGymActivity -> Problem.response(400, Problem.notGymActivity)
                    UpdateGymActivityError.NotAthletesCoach -> Problem.response(403, Problem.notAthletesCoach)
                    UpdateGymActivityError.InvalidSetOrder -> Problem.response(400, Problem.invalidSetOrder)
                    UpdateGymActivityError.InvalidSets -> Problem.response(400, Problem.invalidSets)
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
}

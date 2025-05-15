package waveCoach.http

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import waveCoach.domain.AuthenticatedCoach
import waveCoach.domain.AuthenticatedUser
import waveCoach.http.model.input.CreateGymActivityInputModel
import waveCoach.http.model.output.ActivityWithExercisesOutputModel
import waveCoach.http.model.output.ExerciseWithSetsOutputModel
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
        val result = gymActivityServices.createGymActivity(
            coach.info.id,
            input.athleteId,
            input.date,
            input.exercises.map { inputModel ->
                ExerciseInputInfo(
                    sets = inputModel.sets.map { SetInputInfo(it.reps, it.weight, it.rest) },
                    gymExerciseId = inputModel.gymExerciseId
                )
            }
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
                    CreateGymActivityError.InvalidGymExercise -> Problem.response(400, Problem.invalidGymExercise)
                    CreateGymActivityError.InvalidSet -> Problem.response(400, Problem.invalidSets)
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
                        ActivityWithExercisesOutputModel(
                            result.value.id,
                            result.value.uid,
                            result.value.date,
                            result.value.type.toString(),
                            result.value.exercises.map { exercise ->
                                ExerciseWithSetsOutputModel(
                                    exercise.id,
                                    exercise.activity,
                                    exercise.gymExercise,
                                    exercise.exerciseOrder,
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
                }
        }
    }

    /* @PatchMapping(Uris.Athletes.UPDATE_GYM_ACTIVITY)
    fun updateGymActivity(
        coach: AuthenticatedCoach,
        @PathVariable aid: String,
        @PathVariable activityId: String,
        @RequestBody input: UpdateGymActivityInputModel,
    ): ResponseEntity<*> {
        val uid = aid.toIntOrNull() ?: return Problem.response(400, Problem.invalidAthleteId)
        val activityIdInt = activityId.toIntOrNull() ?: return Problem.response(400, Problem.invalidGymActivityId)
        val result = athleteServices.updateGymActivity(
            coach.info.id,
            uid,
            activityIdInt,
            input.date,
            input.exercises?.map { inputModel ->
                UpdateExerciseInputInfo(
                    inputModel.id,
                    inputModel.sets?.map { UpdateSetInputInfo(it.id ,it.reps, it.weight, it.rest, it.setOrder) },
                    inputModel.gymExerciseId,
                    inputModel.exerciseOrder
                )
            }
        )
        return when (result) {
            is Success -> ResponseEntity.status(204).build<Unit>()

            is Failure ->
                when (result.value) {
                    UpdateGymActivityError.AthleteNotFound -> Problem.response(404, Problem.athleteNotFound)
                    UpdateGymActivityError.InvalidDate -> Problem.response(400, Problem.invalidDate)
                    UpdateGymActivityError.NotAthletesCoach -> Problem.response(403, Problem.notAthletesCoach)
                    UpdateGymActivityError.InvalidGymExercise -> Problem.response(400, Problem.invalidGymExercise)
                    UpdateGymActivityError.InvalidSet -> Problem.response(400, Problem.invalidSets)
                }
        }
    }*/

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
                }
        }
    }
}
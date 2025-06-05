package waveCoach.http

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import waveCoach.domain.AuthenticatedCoach
import waveCoach.domain.AuthenticatedUser
import waveCoach.http.model.input.CreateWaterActivityInputModel
import waveCoach.http.model.input.QuestionnaireCreateInputModel
import waveCoach.http.model.output.ManeuverOutputModel
import waveCoach.http.model.output.Problem
import waveCoach.http.model.output.QuestionnaireOutputModel
import waveCoach.http.model.output.WaterActivityOutputModel
import waveCoach.http.model.output.WaveOutputModel
import waveCoach.services.*
import waveCoach.utils.Failure
import waveCoach.utils.Success

@RestController
class WaterActivityController(
    private val waterActivityService: WaterActivityServices,
) {
    @PostMapping(Uris.WaterActivity.CREATE)
    fun create(
        coach: AuthenticatedCoach,
        @RequestBody input: CreateWaterActivityInputModel,
    ): ResponseEntity<*> {
        val result =
            waterActivityService.createWaterActivity(
                coach.info.id,
                input.athleteId,
                input.date,
                input.rpe,
                input.condition,
                input.trimp,
                input.duration,
                input.waves.map { waveInputModel ->
                    WaveInputInfo(
                        waveInputModel.points,
                        waveInputModel.rightSide,
                        waveInputModel.maneuvers.map { maneuverInputModel ->
                            ManeuverInputInfo(
                                maneuverInputModel.waterManeuverId,
                                maneuverInputModel.success,
                            )
                        },
                    )
                },
            )
        return when (result) {
            is Success ->
                ResponseEntity
                    .status(201)
                    .header("Location", Uris.WaterActivity.byId(result.value).toASCIIString())
                    .build<Unit>()

            is Failure ->
                when (result.value) {
                    CreateWaterActivityError.InvalidDate -> Problem.response(400, Problem.invalidDate)
                    CreateWaterActivityError.AthleteNotFound -> Problem.response(404, Problem.athleteNotFound)
                    CreateWaterActivityError.NotAthletesCoach -> Problem.response(403, Problem.notAthletesCoach)
                    CreateWaterActivityError.ActivityWithoutMicrocycle ->
                        Problem.response(400, Problem.activityWithoutMicrocycle)
                    CreateWaterActivityError.InvalidRpe -> Problem.response(400, Problem.invalidRpe)
                    CreateWaterActivityError.InvalidTrimp -> Problem.response(400, Problem.invalidTrimp)
                    CreateWaterActivityError.InvalidDuration -> Problem.response(400, Problem.invalidDuration)
                    CreateWaterActivityError.InvalidWaterManeuver ->
                        Problem.response(400, Problem.invalidWaterManeuver)
                }
        }
    }

    @GetMapping(Uris.WaterActivity.GET_BY_ID)
    fun getById(
        user: AuthenticatedUser,
        @PathVariable activityId: String,
    ): ResponseEntity<*> {
        val activityIdInt = activityId.toIntOrNull() ?: return Problem.response(400, Problem.invalidWaterActivityId)

        val result = waterActivityService.getWaterActivity(user.info.id, activityIdInt)

        return when (result) {
            is Success ->
                ResponseEntity
                    .status(200)
                    .body(
                        WaterActivityOutputModel(
                            result.value.id,
                            result.value.athleteId,
                            result.value.microcycleId,
                            result.value.date,
                            result.value.rpe,
                            result.value.condition,
                            result.value.trimp,
                            result.value.duration,
                            result.value.waves.map { wave ->
                                WaveOutputModel(
                                    wave.id,
                                    wave.points,
                                    wave.rightSide,
                                    wave.maneuvers.map { maneuver ->
                                        ManeuverOutputModel(
                                            maneuver.id,
                                            maneuver.waterManeuverId,
                                            maneuver.waterManeuverName,
                                            maneuver.url,
                                            maneuver.success,
                                        )
                                    },
                                )
                            },
                        ),
                    )

            is Failure ->
                when (result.value) {
                    GetWaterActivityError.NotAthletesCoach -> Problem.response(403, Problem.notAthletesCoach)
                    GetWaterActivityError.ActivityNotFound -> Problem.response(404, Problem.waterActivityNotFound)
                    GetWaterActivityError.NotWaterActivity -> Problem.response(400, Problem.notWaterActivity)
                }
        }
    }

    @DeleteMapping(Uris.WaterActivity.REMOVE)
    fun remove(
        coach: AuthenticatedCoach,
        @PathVariable activityId: String,
    ): ResponseEntity<*> {
        val activityIdInt = activityId.toIntOrNull() ?: return Problem.response(400, Problem.invalidWaterActivityId)

        val result = waterActivityService.removeWaterActivity(coach.info.id, activityIdInt)

        return when (result) {
            is Success -> ResponseEntity.status(204).build<Unit>()
            is Failure ->
                when (result.value) {
                    RemoveWaterActivityError.NotAthletesCoach -> Problem.response(403, Problem.notAthletesCoach)
                    RemoveWaterActivityError.ActivityNotFound -> Problem.response(404, Problem.waterActivityNotFound)
                    RemoveWaterActivityError.NotWaterActivity -> Problem.response(400, Problem.notWaterActivity)
                }
        }
    }

    @PostMapping(Uris.WaterActivity.CREATE_QUESTIONNAIRE)
    fun createQuestionnaire(
        user: AuthenticatedUser,
        @RequestBody input: QuestionnaireCreateInputModel,
        @PathVariable activityId: String,
    ): ResponseEntity<*> {
        val id = activityId.toIntOrNull() ?: return Problem.response(400, Problem.invalidWaterActivityId)

        val result = waterActivityService.createQuestionnaire(user.info.id, id, input.sleep, input.fatigue, input.stress, input.musclePain)

        return when (result) {
            is Success -> ResponseEntity.status(204).build<Unit>()

            is Failure ->
                when (result.value) {
                    CreateQuestionnaireError.AlreadyExists -> Problem.response(409, Problem.questionnaireAlreadyExists)
                    CreateQuestionnaireError.ActivityNotFound -> Problem.response(404, Problem.waterActivityNotFound)
                    CreateQuestionnaireError.NotAthletesCoach -> Problem.response(403, Problem.notAthletesCoach)
                    CreateQuestionnaireError.InvalidSleep -> Problem.response(400, Problem.invalidSleep)
                    CreateQuestionnaireError.InvalidFatigue -> Problem.response(400, Problem.invalidFatigue)
                    CreateQuestionnaireError.InvalidStress -> Problem.response(400, Problem.invalidStress)
                    CreateQuestionnaireError.InvalidMusclePain -> Problem.response(400, Problem.invalidMusclePain)
                }
        }
    }

    @GetMapping(Uris.WaterActivity.GET_QUESTIONNAIRE)
    fun getQuestionnaire(
        user: AuthenticatedUser,
        @PathVariable activityId: String,
    ): ResponseEntity<*> {
        val id = activityId.toIntOrNull() ?: return Problem.response(400, Problem.invalidWaterActivityId)

        val result = waterActivityService.getQuestionnaire(user.info.id, id)

        return when (result) {
            is Success ->
                ResponseEntity
                    .status(200)
                    .body(
                        QuestionnaireOutputModel(
                            result.value.sleep,
                            result.value.fatigue,
                            result.value.stress,
                            result.value.musclePain,
                        ),
                    )

            is Failure ->
                when (result.value) {
                    GetQuestionnaireError.ActivityNotFound -> Problem.response(404, Problem.waterActivityNotFound)
                    GetQuestionnaireError.NotAthletesCoach -> Problem.response(403, Problem.notAthletesCoach)
                    GetQuestionnaireError.QuestionnaireNotFound -> Problem.response(404, Problem.questionnaireNotFound)
                }
        }
    }
}

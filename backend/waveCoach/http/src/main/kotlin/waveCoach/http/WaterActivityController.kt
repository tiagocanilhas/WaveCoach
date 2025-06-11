package waveCoach.http

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import waveCoach.domain.AuthenticatedCoach
import waveCoach.domain.AuthenticatedUser
import waveCoach.http.model.input.AddManeuverInputModel
import waveCoach.http.model.input.AddWaveInputModel
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

        val result = waterActivityService.createQuestionnaire(
            user.info.id,
            id,
            input.sleep,
            input.fatigue,
            input.stress,
            input.musclePain
        )

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

    @PostMapping(Uris.WaterActivity.ADD_WAVE)
    fun addWave(
        coach: AuthenticatedCoach,
        @PathVariable activityId: String,
        @RequestBody waveInputModel: AddWaveInputModel,
    ): ResponseEntity<*> {
        val id = activityId.toIntOrNull() ?: return Problem.response(400, Problem.invalidWaterActivityId)

        val result = waterActivityService.addWave(
            coach.info.id,
            id,
            waveInputModel.points,
            waveInputModel.rightSide,
            waveInputModel.maneuvers.map { maneuverInputModel ->
                ManeuverInputInfo(
                    maneuverInputModel.waterManeuverId,
                    maneuverInputModel.success,
                )
            },
            waveInputModel.order,
        )

        return when (result) {
            is Success -> ResponseEntity
                .status(201)
                .header("Location", Uris.WaterActivity.waveById(id, result.value).toASCIIString())
                .build<Unit>()

            is Failure ->
                when (result.value) {
                    AddWaveError.ActivityNotFound -> Problem.response(404, Problem.waterActivityNotFound)
                    AddWaveError.NotAthletesCoach -> Problem.response(403, Problem.notAthletesCoach)
                    AddWaveError.InvalidOrder -> Problem.response(400, Problem.invalidOrder)
                    AddWaveError.InvalidWaterManeuver -> Problem.response(400, Problem.invalidWaterManeuver)
                    AddWaveError.NotWaterActivity -> Problem.response(400, Problem.notWaterActivity)
                }
        }
    }

    @DeleteMapping(Uris.WaterActivity.REMOVE_WAVE)
    fun removeWave(
        coach: AuthenticatedCoach,
        @PathVariable activityId: String,
        @PathVariable waveId: String,
    ): ResponseEntity<*> {
        val activityIdInt = activityId.toIntOrNull() ?: return Problem.response(400, Problem.invalidWaterActivityId)
        val waveIdInt = waveId.toIntOrNull() ?: return Problem.response(400, Problem.invalidWaveId)

        val result = waterActivityService.removeWave(coach.info.id, activityIdInt, waveIdInt)

        return when (result) {
            is Success -> ResponseEntity.status(204).build<Unit>()
            is Failure ->
                when (result.value) {
                    RemoveWaveError.ActivityNotFound -> Problem.response(404, Problem.waterActivityNotFound)
                    RemoveWaveError.NotAthletesCoach -> Problem.response(403, Problem.notAthletesCoach)
                    RemoveWaveError.WaveNotFound -> Problem.response(404, Problem.waveNotFound)
                    RemoveWaveError.NotWaterActivity -> Problem.response(400, Problem.notWaterActivity)
                    RemoveWaveError.NotActivityWave -> Problem.response(400, Problem.notActivityWave)
                }
        }
    }

    @PostMapping(Uris.WaterActivity.ADD_MANEUVER)
    fun addManeuver(
        coach: AuthenticatedCoach,
        @PathVariable activityId: String,
        @PathVariable waveId: String,
        @RequestBody maneuverInputModel: AddManeuverInputModel,
    ): ResponseEntity<*> {
        val activityIdInt = activityId.toIntOrNull() ?: return Problem.response(400, Problem.invalidWaterActivityId)
        val waveIdInt = waveId.toIntOrNull() ?: return Problem.response(400, Problem.invalidWaveId)

        val result = waterActivityService.addManeuver(
            coach.info.id,
            activityIdInt,
            waveIdInt,
            maneuverInputModel.waterManeuverId,
            maneuverInputModel.success,
            maneuverInputModel.order,
        )

        return when (result) {
            is Success -> ResponseEntity
                .status(201)
                .header(
                    "Location",
                    Uris.WaterActivity.maneuverById(activityIdInt, waveIdInt, result.value).toASCIIString()
                )
                .build<Unit>()

            is Failure ->
                when (result.value) {
                    AddManeuverError.ActivityNotFound -> Problem.response(404, Problem.waterActivityNotFound)
                    AddManeuverError.NotAthletesCoach -> Problem.response(403, Problem.notAthletesCoach)
                    AddManeuverError.WaveNotFound -> Problem.response(404, Problem.waveNotFound)
                    AddManeuverError.InvalidOrder -> Problem.response(400, Problem.invalidOrder)
                    AddManeuverError.InvalidWaterManeuver -> Problem.response(400, Problem.invalidWaterManeuver)
                    AddManeuverError.NotWaterActivity -> Problem.response(400, Problem.notWaterActivity)
                    AddManeuverError.NotActivityWave -> Problem.response(400, Problem.notActivityWave)
                }
        }
    }

    @DeleteMapping(Uris.WaterActivity.REMOVE_MANEUVER)
    fun removeManeuver(
        coach: AuthenticatedCoach,
        @PathVariable activityId: String,
        @PathVariable waveId: String,
        @PathVariable maneuverId: String,
    ): ResponseEntity<*> {
        val activityIdInt = activityId.toIntOrNull() ?: return Problem.response(400, Problem.invalidWaterActivityId)
        val waveIdInt = waveId.toIntOrNull() ?: return Problem.response(400, Problem.invalidWaveId)
        val maneuverIdInt = maneuverId.toIntOrNull() ?: return Problem.response(400, Problem.invalidManeuverId)

        val result = waterActivityService.removeManeuver(coach.info.id, activityIdInt, waveIdInt, maneuverIdInt)

        return when (result) {
            is Success -> ResponseEntity.status(204).build<Unit>()
            is Failure ->
                when (result.value) {
                    RemoveManeuverError.ActivityNotFound -> Problem.response(404, Problem.waterActivityNotFound)
                    RemoveManeuverError.NotAthletesCoach -> Problem.response(403, Problem.notAthletesCoach)
                    RemoveManeuverError.WaveNotFound -> Problem.response(404, Problem.waveNotFound)
                    RemoveManeuverError.ManeuverNotFound -> Problem.response(404, Problem.maneuverNotFound)
                    RemoveManeuverError.NotWaterActivity -> Problem.response(400, Problem.notWaterActivity)
                    RemoveManeuverError.NotActivityWave -> Problem.response(400, Problem.notActivityWave)
                    RemoveManeuverError.NotWaveManeuver -> Problem.response(400, Problem.notWaveManeuver)
                }
        }
    }
}

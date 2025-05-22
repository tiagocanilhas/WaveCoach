package waveCoach.http

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import waveCoach.domain.AuthenticatedCoach
import waveCoach.http.model.input.CreateWaterActivityInputModel
import waveCoach.http.model.output.Problem
import waveCoach.services.CreateWaterActivityError
import waveCoach.services.ManeuverInputInfo
import waveCoach.services.WaterActivityServices
import waveCoach.services.WaveInputInfo
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
        val result = waterActivityService.createWaterActivity(
            coach.info.id,
            input.athleteId,
            input.date,
            input.pse,
            input.condition,
            input.heartRate,
            input.duration,
            input.waves.map { waveInputModel ->
                WaveInputInfo(
                    waveInputModel.points,
                    waveInputModel.maneuvers.map { maneuverInputModel ->
                        ManeuverInputInfo(
                            maneuverInputModel.waterManeuverId,
                            maneuverInputModel.rightSide,
                            maneuverInputModel.success
                        )
                    }
                )
            }
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
                    CreateWaterActivityError.ActivityWithoutMicrocycle -> Problem.response(
                        400,
                        Problem.activityWithoutMicrocycle
                    )

                    CreateWaterActivityError.InvalidPse -> Problem.response(400, Problem.invalidPse)
                    CreateWaterActivityError.InvalidHeartRate -> Problem.response(400, Problem.invalidHeartRate)
                    CreateWaterActivityError.InvalidDuration -> Problem.response(400, Problem.invalidDuration)
                    CreateWaterActivityError.InvalidWaterManeuver -> Problem.response(
                        400,
                        Problem.invalidWaterManeuver
                    )
                }
        }
    }
}
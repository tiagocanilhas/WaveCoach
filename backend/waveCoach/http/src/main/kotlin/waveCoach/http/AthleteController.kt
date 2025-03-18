package waveCoach.http

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import waveCoach.domain.AuthenticatedUser
import waveCoach.http.model.input.AthleteCreateCharacteristicsInputModel
import waveCoach.http.model.input.AthleteCreateInputModel
import waveCoach.http.model.output.CreateCharacteristicsOutputModel
import waveCoach.http.model.output.Problem
import waveCoach.services.AthleteServices
import waveCoach.services.CreateAthleteError
import waveCoach.services.CreateCharacteristicsError
import waveCoach.services.RemoveAthleteError
import waveCoach.utils.Failure
import waveCoach.utils.Success

@RestController
class AthleteController(
    private val athleteServices: AthleteServices,
) {
    @PostMapping(Uris.Athletes.CREATE)
    fun createAthlete(
        coach: AuthenticatedUser,
        @RequestBody input: AthleteCreateInputModel
    ): ResponseEntity<*> {
        val result = athleteServices.createAthlete(input.name, coach.info.id, input.birthDate)
        return when (result) {
            is Success -> ResponseEntity
                .status(201)
                .header("Location", Uris.Athletes.byId(result.value).toASCIIString())
                .build<Unit>()

            is Failure -> when (result.value) {
                CreateAthleteError.InvalidBirthDate -> Problem.response(400, Problem.invalidBirthDate)
                CreateAthleteError.InvalidName -> Problem.response(400, Problem.invalidName)
            }
        }
    }

    @DeleteMapping(Uris.Athletes.REMOVE)
    fun removeAthlete(
        coach: AuthenticatedUser,
        @PathVariable aid: String
    ): ResponseEntity<*> {
        val uid = aid.toIntOrNull() ?: return Problem.response(400, Problem.invalidAthleteId)
        val result = athleteServices.removeAthlete(coach.info.id, uid)
        return when (result) {
            is Success -> ResponseEntity.status(204).build<Unit>()
            is Failure -> when (result.value) {
                RemoveAthleteError.AthleteNotFound -> Problem.response(404, Problem.athleteNotFound)
                RemoveAthleteError.NotAthletesCoach -> Problem.response(403, Problem.notAthletesCoach)
            }
        }
    }

    @PostMapping(Uris.Athletes.CREATE_CHARACTERISTICS)
    fun createCharacteristics(
        coach: AuthenticatedUser,
        @PathVariable aid: String,
        @RequestBody input: AthleteCreateCharacteristicsInputModel
    ): ResponseEntity<*> {
        val uid = aid.toIntOrNull() ?: return Problem.response(400, Problem.invalidAthleteId)
        val result = athleteServices.createCharacteristics(coach.info.id, uid, input.date, input.height, input.weight, input.calories,
            input.waist, input.arm, input.thigh, input.tricep, input.abdominal)

        return when (result) {
            is Success -> ResponseEntity.status(201)
                .body(CreateCharacteristicsOutputModel(result.value))

            is Failure -> when (result.value) {
                CreateCharacteristicsError.AthleteNotFound -> Problem.response(404, Problem.athleteNotFound)
                CreateCharacteristicsError.InvalidCharacteristics -> Problem.response(400, Problem.invalidCharacteristics)
                CreateCharacteristicsError.InvalidDate -> Problem.response(400, Problem.invalidDate)
                CreateCharacteristicsError.NotAthletesCoach -> Problem.response(403, Problem.notAthletesCoach)
            }
        }
    }
}
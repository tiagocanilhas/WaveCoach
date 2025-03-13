package waveCoach.http

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import waveCoach.domain.AuthenticatedUser
import waveCoach.http.model.input.AthleteCreateInputModel
import waveCoach.http.model.output.Problem
import waveCoach.services.AthleteServices
import waveCoach.services.CreateAthleteError
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
                .header("Location", Uris.Users.byId(result.value).toASCIIString())
                .build<Unit>()

            is Failure -> when (result.value) {
                CreateAthleteError.InvalidBirthDate -> Problem.response(400, Problem.invalidBirthDate)
                CreateAthleteError.InvalidName -> Problem.response(400, Problem.invalidName)
            }
        }
    }
}
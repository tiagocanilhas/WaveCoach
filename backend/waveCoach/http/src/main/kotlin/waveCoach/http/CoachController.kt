package waveCoach.http

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import waveCoach.http.model.input.CoachCreateInputModel
import waveCoach.http.model.output.Problem
import waveCoach.services.CoachServices
import waveCoach.services.CreateCoachError
import waveCoach.utils.Failure
import waveCoach.utils.Success

@RestController
class CoachController(
    private val coachServices: CoachServices,
) {
    @PostMapping(Uris.Coaches.CREATE)
    fun create(
        @RequestBody input: CoachCreateInputModel,
    ): ResponseEntity<*> {
        val result = coachServices.createCoach(input.username, input.password)

        return when (result) {
            is Success ->
                ResponseEntity
                    .status(201)
                    .header("Location", Uris.Coaches.byId(result.value).toASCIIString())
                    .build<Unit>()

            is Failure ->
                when (result.value) {
                    CreateCoachError.InvalidUsername -> Problem.response(400, Problem.invalidUsername)
                    CreateCoachError.InsecurePassword -> Problem.response(400, Problem.insecurePassword)
                    CreateCoachError.UsernameAlreadyExists -> Problem.response(400, Problem.usernameAlreadyExists)
                }
        }
    }
}

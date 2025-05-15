package waveCoach.http

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import waveCoach.domain.AuthenticatedCoach
import waveCoach.domain.AuthenticatedUser
import waveCoach.http.model.input.CreateGymExerciseInputModel
import waveCoach.http.model.input.UpdateGymExerciseInputModel
import waveCoach.http.model.output.GymExerciseListOutputModel
import waveCoach.http.model.output.GymExerciseOutputModel
import waveCoach.http.model.output.Problem
import waveCoach.services.CreateGymExerciseError
import waveCoach.services.GymExerciseServices
import waveCoach.services.UpdateGymExerciseError
import waveCoach.utils.Failure
import waveCoach.utils.Success

@RestController
class GymExerciseController(
    private val gymExerciseServices: GymExerciseServices,
) {

    @PostMapping(Uris.GymExercise.CREATE)
    fun create(
        coach: AuthenticatedCoach,
        @RequestBody input: CreateGymExerciseInputModel,
    ): ResponseEntity<*> {
        val result = gymExerciseServices.createGymExercise(input.name, input.category)

        return when (result) {
            is Success ->
                ResponseEntity
                    .status(201)
                    .header("Location", Uris.GymExercise.byId(result.value).toASCIIString())
                    .build<Unit>()

            is Failure ->
                when (result.value) {
                    CreateGymExerciseError.InvalidName -> Problem.response(400, Problem.invalidName)
                    CreateGymExerciseError.InvalidCategory -> Problem.response(400, Problem.invalidCategory)
                    CreateGymExerciseError.NameAlreadyExists -> Problem.response(400, Problem.nameAlreadyExists)
                }
        }
    }

    @GetMapping(Uris.GymExercise.GET_ALL)
    fun getAll(
        coach: AuthenticatedCoach,
    ): ResponseEntity<*> {
        val result = gymExerciseServices.getAllGymExercises()

        return ResponseEntity
            .status(200)
            .body(
                GymExerciseListOutputModel(
                    result.map {
                        GymExerciseOutputModel(
                            it.id,
                            it.name,
                            it.category.toString(),
                        )
                    }
                )
            )
    }

    @PutMapping(Uris.GymExercise.UPDATE)
    fun update(
        coach: AuthenticatedCoach,
        @PathVariable geid: Int,
        @RequestBody input: UpdateGymExerciseInputModel,
    ): ResponseEntity<*> {
        val result = gymExerciseServices.updateGymExercise(geid, input.name, input.category)

        return when (result) {
            is Success -> ResponseEntity.status(204).build<Unit>()
            is Failure ->
                when (result.value) {
                    UpdateGymExerciseError.GymExerciseNotFound -> Problem.response(404, Problem.gymExerciseNotFound)
                    UpdateGymExerciseError.InvalidName -> Problem.response(400, Problem.invalidName)
                    UpdateGymExerciseError.InvalidCategory -> Problem.response(400, Problem.invalidCategory)
                    UpdateGymExerciseError.NameAlreadyExists -> Problem.response(400, Problem.nameAlreadyExists)

                }
        }
    }

    @DeleteMapping(Uris.GymExercise.REMOVE)
    fun remove(
        coach: AuthenticatedCoach,
        @PathVariable geid: Int,
    ): ResponseEntity<*> {
        val result = gymExerciseServices.removeGymExercise(geid)

        return when (result) {
            is Success -> ResponseEntity.status(204).build<Unit>()

            is Failure -> Problem.response(404, Problem.gymExerciseNotFound)
        }
    }
}
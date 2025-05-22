package waveCoach.http

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import waveCoach.domain.AuthenticatedCoach
import waveCoach.domain.AuthenticatedUser
import waveCoach.http.model.input.WaterManeuverCreateInputModel
import waveCoach.http.model.output.Problem
import waveCoach.http.model.output.WaterManeuverListOutputModel
import waveCoach.http.model.output.WaterManeuverOutputModel
import waveCoach.services.CreateWaterManeuverError
import waveCoach.services.WaterManeuverServices
import waveCoach.utils.Failure
import waveCoach.utils.Success

@RestController
class WaterManeuversController(
    private val services: WaterManeuverServices,
) {

    @PostMapping(Uris.WaterManeuver.CREATE)
    fun create(
        coach: AuthenticatedCoach,
        @RequestPart("input") input: WaterManeuverCreateInputModel,
        @RequestPart("photo") photo: MultipartFile?
    ): ResponseEntity<*>{
        val result = services.createWaterManeuver(input.name, photo)

        return when (result){
            is Success ->
                ResponseEntity
                    .status(201)
                    .header("Location", Uris.WaterManeuver.byId(result.value).toASCIIString())
                    .build<Unit>()

            is Failure ->
                when (result.value) {
                    CreateWaterManeuverError.InvalidName -> Problem.response(400, Problem.invalidName)
                    CreateWaterManeuverError.NameAlreadyExists -> Problem.response(400, Problem.nameAlreadyExists)
                    CreateWaterManeuverError.InvalidPhoto -> Problem.response(400, Problem.invalidPhoto)
                }
        }
    }

    @GetMapping(Uris.WaterManeuver.GET_ALL)
    fun getAll(
        user: AuthenticatedUser
    ): ResponseEntity<*> {
        val result = services.getWaterManeuvers()

        return ResponseEntity
            .status(200)
            .body(
                WaterManeuverListOutputModel(
                    result.map {
                        WaterManeuverOutputModel(
                            it.id,
                            it.name,
                            it.url
                        )
                    }
                )
            )
    }

    @PutMapping(Uris.WaterManeuver.UPDATE)
    fun update(
        coach: AuthenticatedCoach,
        @PathVariable geid: Int,
    ): ResponseEntity<*> {
        return TODO("Implement the update method")
    }


    @DeleteMapping(Uris.WaterManeuver.REMOVE)
    fun remove(
        coach: AuthenticatedCoach,
        @PathVariable geid: Int,
    ): ResponseEntity<*> {
        return TODO("Implement the remove method")
    }

}
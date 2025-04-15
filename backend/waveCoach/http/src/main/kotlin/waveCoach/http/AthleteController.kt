package waveCoach.http

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import waveCoach.domain.AuthenticatedUser
import waveCoach.http.model.input.*
import waveCoach.http.model.output.*
import waveCoach.services.*
import waveCoach.utils.Failure
import waveCoach.utils.Success

@RestController
class AthleteController(
    private val athleteServices: AthleteServices,
) {
    @PostMapping(Uris.Athletes.CREATE)
    fun create(
        coach: AuthenticatedUser,
        @RequestBody input: AthleteCreateInputModel,
    ): ResponseEntity<*> {
        val result = athleteServices.createAthlete(input.name, coach.info.id, input.birthDate)

        return when (result) {
            is Success ->
                ResponseEntity
                    .status(201)
                    .header("Location", Uris.Athletes.byId(result.value).toASCIIString())
                    .build<Unit>()

            is Failure ->
                when (result.value) {
                    CreateAthleteError.InvalidBirthDate -> Problem.response(400, Problem.invalidBirthDate)
                    CreateAthleteError.InvalidName -> Problem.response(400, Problem.invalidName)
                }
        }
    }

    @GetMapping(Uris.Athletes.GET_BY_ID)
    fun getById(
        coach: AuthenticatedUser,
        @PathVariable aid: String,
    ): ResponseEntity<*> {
        val uid = aid.toIntOrNull() ?: return Problem.response(400, Problem.invalidAthleteId)
        val result = athleteServices.getAthlete(coach.info.id, uid)

        return when (result) {
            is Success ->
                ResponseEntity
                    .status(200)
                    .body(
                        AthleteOutputModel(
                            result.value.uid,
                            result.value.coach,
                            result.value.name,
                            result.value.birthDate,
                        ),
                    )

            is Failure ->
                when (result.value) {
                    GetAthleteError.AthleteNotFound -> Problem.response(404, Problem.athleteNotFound)
                    GetAthleteError.NotAthletesCoach -> Problem.response(403, Problem.notAthletesCoach)
                }
        }
    }

    @GetMapping(Uris.Athletes.GET_BY_COACH)
    fun getByCoach(coach: AuthenticatedUser): ResponseEntity<*> {
        val result = athleteServices.getAthletes(coach.info.id)

        return ResponseEntity
            .status(200)
            .body(
                AthleteListOutputModel(
                    result.map {
                        AthleteOutputModel(
                            it.uid,
                            it.coach,
                            it.name,
                            it.birthDate,
                        )
                    },
                ),
            )
    }

    @PutMapping(Uris.Athletes.UPDATE)
    fun update(
        coach: AuthenticatedUser,
        @PathVariable aid: String,
        @RequestBody input: AthleteUpdateInputModel,
    ): ResponseEntity<*> {
        val uid = aid.toIntOrNull() ?: return Problem.response(400, Problem.invalidAthleteId)
        val result = athleteServices.updateAthlete(coach.info.id, uid, input.name, input.birthDate)

        return when (result) {
            is Success ->
                ResponseEntity
                    .status(204)
                    .build<Unit>()

            is Failure ->
                when (result.value) {
                    UpdateAthleteError.AthleteNotFound -> Problem.response(404, Problem.athleteNotFound)
                    UpdateAthleteError.InvalidBirthDate -> Problem.response(400, Problem.invalidBirthDate)
                    UpdateAthleteError.InvalidName -> Problem.response(400, Problem.invalidName)
                    UpdateAthleteError.NotAthletesCoach -> Problem.response(403, Problem.notAthletesCoach)
                }
        }
    }

    @DeleteMapping(Uris.Athletes.REMOVE)
    fun remove(
        coach: AuthenticatedUser,
        @PathVariable aid: String,
    ): ResponseEntity<*> {
        val uid = aid.toIntOrNull() ?: return Problem.response(400, Problem.invalidAthleteId)
        val result = athleteServices.removeAthlete(coach.info.id, uid)

        return when (result) {
            is Success ->
                ResponseEntity
                    .status(204)
                    .build<Unit>()

            is Failure ->
                when (result.value) {
                    RemoveAthleteError.AthleteNotFound -> Problem.response(404, Problem.athleteNotFound)
                    RemoveAthleteError.NotAthletesCoach -> Problem.response(403, Problem.notAthletesCoach)
                }
        }
    }

    @PostMapping(Uris.Athletes.GENERATE_CODE)
    fun generateCode(
        coach: AuthenticatedUser,
        @PathVariable aid: String,
    ): ResponseEntity<*> {
        val uid = aid.toIntOrNull() ?: return Problem.response(400, Problem.invalidAthleteId)
        val result = athleteServices.generateCode(coach.info.id, uid)

        return when (result) {
            is Success ->
                ResponseEntity
                    .status(201)
                    .body(
                        AthleteCodeOutputModel(
                            result.value.code,
                            result.value.expirationDate.epochSeconds,
                        ),
                    )

            is Failure ->
                when (result.value) {
                    GenerateCodeError.AthleteNotFound -> Problem.response(404, Problem.athleteNotFound)
                    GenerateCodeError.NotAthletesCoach -> Problem.response(403, Problem.notAthletesCoach)
                }
        }
    }

    @GetMapping(Uris.Athletes.GET_BY_CODE)
    fun getByCode(
        @PathVariable code: String,
    ): ResponseEntity<*> {
        val result = athleteServices.getUsernameByCode(code)

        return when (result) {
            is Success ->
                ResponseEntity
                    .status(200)
                    .body(AthleteByCodeOutputModel(result.value))

            is Failure ->
                when (result.value) {
                    GetUsernameByCodeError.InvalidCode -> Problem.response(400, Problem.invalidCode)
                }
        }
    }

    @PostMapping(Uris.Athletes.CHANGE_CREDENTIALS)
    fun changeCredentials(
        @RequestBody input: AthleteChangeCredentialsInputModel,
    ): ResponseEntity<*> {
        val result = athleteServices.changeCredentials(input.code, input.username, input.password)

        return when (result) {
            is Success ->
                ResponseEntity
                    .status(204)
                    .build<Unit>()

            is Failure ->
                when (result.value) {
                    ChangeCredentialsError.InvalidUsername -> Problem.response(400, Problem.invalidUsername)
                    ChangeCredentialsError.UsernameAlreadyExists -> Problem.response(400, Problem.usernameAlreadyExists)
                    ChangeCredentialsError.InsecurePassword -> Problem.response(400, Problem.insecurePassword)
                    ChangeCredentialsError.InvalidCode -> Problem.response(400, Problem.invalidCode)
                }
        }
    }

    @PostMapping(Uris.Athletes.CREATE_CHARACTERISTICS)
    fun createCharacteristics(
        coach: AuthenticatedUser,
        @PathVariable aid: String,
        @RequestBody input: AthleteCreateCharacteristicsInputModel,
    ): ResponseEntity<*> {
        val uid = aid.toIntOrNull() ?: return Problem.response(400, Problem.invalidAthleteId)
        val result =
            athleteServices.createCharacteristics(
                coach.info.id, uid, input.date, input.height, input.weight, input.calories, input.bodyFat,
                input.waistSize, input.armSize, input.thighSize, input.tricepFat, input.abdomenFat, input.thighFat,
            )

        return when (result) {
            is Success ->
                ResponseEntity
                    .status(201)
                    .header("Location", Uris.Athletes.characteristicsByDate(uid, result.value).toASCIIString())
                    .build<Unit>()

            is Failure ->
                when (result.value) {
                    CreateCharacteristicsError.AthleteNotFound -> Problem.response(404, Problem.athleteNotFound)
                    CreateCharacteristicsError.CharacteristicsAlreadyExists ->
                        Problem.response(409, Problem.characteristicsAlreadyExists)

                    CreateCharacteristicsError.InvalidCharacteristics ->
                        Problem.response(400, Problem.invalidCharacteristics)

                    CreateCharacteristicsError.InvalidDate -> Problem.response(400, Problem.invalidDate)
                    CreateCharacteristicsError.NotAthletesCoach -> Problem.response(403, Problem.notAthletesCoach)
                }
        }
    }

    @GetMapping(Uris.Athletes.GET_CHARACTERISTICS)
    fun getCharacteristics(
        coach: AuthenticatedUser,
        @PathVariable aid: String,
        @PathVariable date: String,
    ): ResponseEntity<*> {
        val uid = aid.toIntOrNull() ?: return Problem.response(400, Problem.invalidAthleteId)
        val result = athleteServices.getCharacteristics(coach.info.id, uid, date)

        return when (result) {
            is Success ->
                ResponseEntity
                    .status(200)
                    .body(
                        CharacteristicsOutputModel(
                            result.value.uid,
                            result.value.date,
                            result.value.weight,
                            result.value.height,
                            result.value.bmi,
                            result.value.calories,
                            result.value.bodyFat,
                            result.value.waistSize,
                            result.value.armSize,
                            result.value.thighSize,
                            result.value.tricepFat,
                            result.value.abdomenFat,
                            result.value.thighFat,
                        ),
                    )

            is Failure ->
                when (result.value) {
                    GetCharacteristicsError.InvalidDate -> Problem.response(400, Problem.invalidDate)
                    GetCharacteristicsError.AthleteNotFound -> Problem.response(404, Problem.athleteNotFound)
                    GetCharacteristicsError.CharacteristicsNotFound ->
                        Problem.response(404, Problem.characteristicsNotFound)

                    GetCharacteristicsError.NotAthletesCoach -> Problem.response(403, Problem.notAthletesCoach)
                }
        }
    }

    @GetMapping(Uris.Athletes.GET_CHARACTERISTICS_LIST)
    fun getCharacteristicsList(
        coach: AuthenticatedUser,
        @PathVariable aid: String,
    ): ResponseEntity<*> {
        val uid = aid.toIntOrNull() ?: return Problem.response(400, Problem.invalidAthleteId)
        val result = athleteServices.getCharacteristicsList(coach.info.id, uid)

        return when (result) {
            is Success ->
                ResponseEntity
                    .status(200)
                    .body(
                        CharacteristicsListOutputModel(
                            result.value.map {
                                CharacteristicsOutputModel(
                                    it.uid,
                                    it.date,
                                    it.weight,
                                    it.height,
                                    it.bmi,
                                    it.calories,
                                    it.bodyFat,
                                    it.waistSize,
                                    it.armSize,
                                    it.thighSize,
                                    it.tricepFat,
                                    it.abdomenFat,
                                    it.thighFat,
                                )
                            },
                        ),
                    )

            is Failure ->
                when (result.value) {
                    GetCharacteristicsListError.AthleteNotFound -> Problem.response(404, Problem.athleteNotFound)
                    GetCharacteristicsListError.NotAthletesCoach -> Problem.response(403, Problem.notAthletesCoach)
                }
        }
    }

    @PutMapping(Uris.Athletes.UPDATE_CHARACTERISTICS)
    fun updateCharacteristics(
        coach: AuthenticatedUser,
        @PathVariable aid: String,
        @PathVariable date: String,
        @RequestBody input: AthleteUpdateCharacteristicsInputModel,
    ): ResponseEntity<*> {
        val uid = aid.toIntOrNull() ?: return Problem.response(400, Problem.invalidAthleteId)
        val result =
            athleteServices.updateCharacteristics(
                coach.info.id, uid, date, input.height, input.weight, input.calories, input.bodyFat,
                input.waistSize, input.armSize, input.thighSize, input.tricepFat, input.abdomenFat, input.thighFat,
            )

        return when (result) {
            is Success ->
                ResponseEntity
                    .status(204)
                    .build<Unit>()

            is Failure ->
                when (result.value) {
                    UpdateCharacteristicsError.AthleteNotFound -> Problem.response(404, Problem.athleteNotFound)
                    UpdateCharacteristicsError.InvalidCharacteristics ->
                        Problem.response(400, Problem.invalidCharacteristics)

                    UpdateCharacteristicsError.InvalidDate -> Problem.response(400, Problem.invalidDate)
                    UpdateCharacteristicsError.NotAthletesCoach -> Problem.response(403, Problem.notAthletesCoach)
                }
        }
    }

    @DeleteMapping(Uris.Athletes.REMOVE_CHARACTERISTICS)
    fun removeCharacteristics(
        coach: AuthenticatedUser,
        @PathVariable aid: String,
        @PathVariable date: String,
    ): ResponseEntity<*> {
        val uid = aid.toIntOrNull() ?: return Problem.response(400, Problem.invalidAthleteId)
        val result = athleteServices.removeCharacteristics(coach.info.id, uid, date)

        return when (result) {
            is Success ->
                ResponseEntity
                    .status(204)
                    .build<Unit>()

            is Failure ->
                when (result.value) {
                    RemoveCharacteristicsError.InvalidDate -> Problem.response(400, Problem.invalidDate)
                    RemoveCharacteristicsError.AthleteNotFound -> Problem.response(404, Problem.athleteNotFound)
                    RemoveCharacteristicsError.CharacteristicsNotFound ->
                        Problem.response(404, Problem.characteristicsNotFound)

                    RemoveCharacteristicsError.NotAthletesCoach -> Problem.response(403, Problem.notAthletesCoach)
                }
        }
    }

    @PostMapping(Uris.Athletes.CREATE_GYM_ACTIVITY)
    fun createGymActivity(
        coach: AuthenticatedUser,
        @PathVariable aid: String,
        @RequestBody input: CreateGymActivityInputModel,
    ): ResponseEntity<*> {
        val uid = aid.toIntOrNull() ?: return Problem.response(400, Problem.invalidAthleteId)
        val result = athleteServices.createGymActivity(coach.info.id, uid, input.date)

        return when (result) {
            is Success ->
                ResponseEntity
                    .status(201)
                    .header("Location", Uris.Athletes.gymActivityById(uid, result.value).toASCIIString())
                    .build<Unit>()

            is Failure ->
                when (result.value) {
                    CreateGymActivityError.AthleteNotFound -> Problem.response(404, Problem.athleteNotFound)
                    CreateGymActivityError.InvalidDate -> Problem.response(400, Problem.invalidDate)
                    CreateGymActivityError.NotAthletesCoach -> Problem.response(403, Problem.notAthletesCoach)
                }
        }
    }

    /*@PostMapping(Uris.Athletes.CREATE_WATER_ACTIVITY)
    fun createWaterActivity(
        coach: AuthenticatedUser,
        @PathVariable aid: String,
        @RequestBody input: AthleteCreateWaterActivityInputModel
    ): ResponseEntity<*> {
        val uid = aid.toIntOrNull() ?: return Problem.response(400, Problem.invalidAthleteId)
        val result = athleteServices.createWaterActivity(
            coach.info.id, uid, input.date, input.duration, input.distance, input.calories
        )

        return when (result) {
            is Success -> ResponseEntity
                .status(201)
                .header("Location", Uris.Athletes.characteristicsByDate(uid, result.value).toASCIIString())
                .build<Unit>()

            is Failure -> when (result.value) {
                CreateWaterActivityError.AthleteNotFound -> Problem.response(404, Problem.athleteNotFound)
                CreateWaterActivityError.InvalidDate -> Problem.response(400, Problem.invalidDate)
                CreateWaterActivityError.NotAthletesCoach -> Problem.response(403, Problem.notAthletesCoach)
            }
        }
    }*/
}

package waveCoach.http

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import waveCoach.domain.AuthenticatedCoach
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
    @PostMapping(Uris.Athletes.CREATE, consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun create(
        coach: AuthenticatedCoach,
        @RequestPart("input") input: AthleteCreateInputModel,
        @RequestPart("photo") photo: MultipartFile?,
    ): ResponseEntity<*> {
        val result = athleteServices.createAthlete(input.name, coach.info.id, input.birthDate, photo)

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
                    CreateAthleteError.InvalidPhoto -> Problem.response(400, Problem.invalidPhoto)
                }
        }
    }

    @GetMapping(Uris.Athletes.GET_BY_ID)
    fun getById(
        user: AuthenticatedUser,
        @PathVariable aid: String,
    ): ResponseEntity<*> {
        val uid = aid.toIntOrNull() ?: return Problem.response(400, Problem.invalidAthleteId)
        val result = athleteServices.getAthlete(user.info.id, uid)

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
                            result.value.credentialsChanged,
                            result.value.url,
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
    fun getByCoach(coach: AuthenticatedCoach): ResponseEntity<*> {
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
                            it.credentialsChanged,
                            it.url,
                        )
                    },
                ),
            )
    }

    @PutMapping(Uris.Athletes.UPDATE)
    fun update(
        coach: AuthenticatedCoach,
        @PathVariable aid: String,
        @RequestBody input: AthleteUpdateInputModel,
    ): ResponseEntity<*> {
        val uid = aid.toIntOrNull() ?: return Problem.response(400, Problem.invalidAthleteId)
        val result = athleteServices.updateAthlete(coach.info.id, uid, input.name, input.birthDate)

        return when (result) {
            is Success -> ResponseEntity.status(204).build<Unit>()

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
        coach: AuthenticatedCoach,
        @PathVariable aid: String,
    ): ResponseEntity<*> {
        val uid = aid.toIntOrNull() ?: return Problem.response(400, Problem.invalidAthleteId)
        val result = athleteServices.removeAthlete(coach.info.id, uid)

        return when (result) {
            is Success -> ResponseEntity.status(204).build<Unit>()

            is Failure ->
                when (result.value) {
                    RemoveAthleteError.AthleteNotFound -> Problem.response(404, Problem.athleteNotFound)
                    RemoveAthleteError.NotAthletesCoach -> Problem.response(403, Problem.notAthletesCoach)
                }
        }
    }

    @PostMapping(Uris.Athletes.GENERATE_CODE)
    fun generateCode(
        coach: AuthenticatedCoach,
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
                    GenerateCodeError.CredentialsAlreadyChanged ->
                        Problem.response(409, Problem.credentialsAlreadyChanged)
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
        coach: AuthenticatedCoach,
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
        user: AuthenticatedUser,
        @PathVariable aid: String,
        @PathVariable date: String,
    ): ResponseEntity<*> {
        val uid = aid.toIntOrNull() ?: return Problem.response(400, Problem.invalidAthleteId)
        val result = athleteServices.getCharacteristics(user.info.id, uid, date)

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
        coach: AuthenticatedCoach,
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
        coach: AuthenticatedCoach,
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

    @PostMapping(Uris.Athletes.CREATE_CALENDAR)
    fun createCalendar(
        coach: AuthenticatedCoach,
        @PathVariable aid: String,
        @RequestBody input: CalendarCreateInputModel,
    ): ResponseEntity<*> {
        val uid = aid.toIntOrNull() ?: return Problem.response(400, Problem.invalidAthleteId)

        val mesocycles =
            input.mesocycles.map { mesocycle ->
                MesocycleInputInfo(
                    mesocycle.id,
                    mesocycle.startTime,
                    mesocycle.endTime,
                    mesocycle.microcycles.map { microcycle ->
                        MicrocycleInputInfo(
                            microcycle.id,
                            microcycle.startTime,
                            microcycle.endTime,
                        )
                    },
                )
            }

        val result = athleteServices.setCalendar(coach.info.id, uid, mesocycles)

        return when (result) {
            is Success ->
                ResponseEntity
                    .status(201)
//                    .header("Location", Uris.Athletes.getCalendar(uid).toASCIIString())
                    .build<Unit>()

            is Failure ->
                when (result.value) {
                    SetCalendarError.MesocycleNotFound -> Problem.response(404, Problem.athleteNotFound)
                    SetCalendarError.InvalidMesocycle -> Problem.response(400, Problem.athleteNotFound)
                    SetCalendarError.MicrocycleNotFound -> Problem.response(404, Problem.athleteNotFound)
                    SetCalendarError.InvalidMicrocycle -> Problem.response(400, Problem.athleteNotFound)
                    SetCalendarError.AthleteNotFound -> Problem.response(404, Problem.athleteNotFound)
                    SetCalendarError.NotAthletesCoach -> Problem.response(403, Problem.notAthletesCoach)
                }
        }
    }

    @GetMapping(Uris.Athletes.GET_CALENDAR)
    fun getCalendar(
        user: AuthenticatedUser,
        @PathVariable aid: String,
        @RequestParam type: String?,
    ): ResponseEntity<*> {
        val uid = aid.toIntOrNull() ?: return Problem.response(400, Problem.invalidAthleteId)
        val result = athleteServices.getCalendar(user.info.id, uid, type)

        return when (result) {
            is Success ->
                ResponseEntity
                    .status(200)
                    .body(
                        CalendarOutputModel(
                            result.value.map { meso ->
                                MesocycleOutputModel(
                                    meso.id,
                                    meso.startTime,
                                    meso.endTime,
                                    meso.microcycles.map { micro ->
                                        MicrocycleOutputModel(
                                            micro.id,
                                            micro.startTime,
                                            micro.endTime,
                                            micro.activities.map {
                                                ActivityOutputModel(
                                                    it.id,
                                                    it.date,
                                                    it.type.toString(),
                                                )
                                            },
                                        )
                                    },
                                )
                            },
                        ),
                    )

            is Failure ->
                when (result.value) {
                    GetCalendarError.AthleteNotFound -> Problem.response(404, Problem.athleteNotFound)
                    GetCalendarError.NotAthletesCoach -> Problem.response(403, Problem.notAthletesCoach)
                }
        }
    }

    @GetMapping(Uris.Athletes.GET_WATER_ACTIVITIES)
    fun getWaterActivities(
        user: AuthenticatedUser,
        @PathVariable aid: String,
    ): ResponseEntity<*> {
        val id = aid.toIntOrNull() ?: return Problem.response(400, Problem.invalidAthleteId)
        val result = athleteServices.getWaterActivities(user.info.id, id)

        return when (result) {
            is Success ->
                ResponseEntity
                    .status(200)
                    .body(
                        WaterActivitiesCalendar(
                            result.value.map { mesocycle ->
                                MesocycleWaterOutputModel(
                                    mesocycle.id,
                                    mesocycle.startTime,
                                    mesocycle.endTime,
                                    mesocycle.microcycles.map { microcycle ->
                                        MicrocycleWaterOutputModel(
                                            microcycle.id,
                                            microcycle.startTime,
                                            microcycle.endTime,
                                            microcycle.activities.map { waterActivity ->
                                                WaterActivityOutputModel(
                                                    waterActivity.id,
                                                    waterActivity.athleteId,
                                                    waterActivity.microcycleId,
                                                    waterActivity.date,
                                                    waterActivity.rpe,
                                                    waterActivity.condition,
                                                    waterActivity.trimp,
                                                    waterActivity.duration,
                                                    waterActivity.waves.map { wave ->
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
                                                )
                                            },
                                        )
                                    },
                                )
                            },
                        ),
                    )

            is Failure ->
                when (result.value) {
                    GetWaterActivitiesError.AthleteNotFound -> Problem.response(404, Problem.athleteNotFound)
                    GetWaterActivitiesError.NotAthletesCoach -> Problem.response(403, Problem.notAthletesCoach)
                }
        }
    }

    @PostMapping(Uris.Athletes.CREATE_COMPETITION)
    fun createCompetition(
        coach: AuthenticatedCoach,
        @PathVariable aid: String,
        @RequestBody input: CreateCompetitionInputModel,
    ): ResponseEntity<*> {
        val uid = aid.toIntOrNull() ?: return Problem.response(400, Problem.invalidAthleteId)
        val result = athleteServices.createCompetition(coach.info.id, uid, input.date, input.location)

        return when (result) {
            is Success ->
                ResponseEntity
                    .status(201)
                    .header("Location", Uris.Athletes.competitionById(uid, result.value).toASCIIString())
                    .build<Unit>()

            is Failure ->
                when (result.value) {
                    CreateCompetitionError.AthleteNotFound -> Problem.response(404, Problem.athleteNotFound)
                    CreateCompetitionError.InvalidDate -> Problem.response(400, Problem.invalidDate)
                    CreateCompetitionError.NotAthletesCoach -> Problem.response(403, Problem.notAthletesCoach)
                }
        }
    }
}

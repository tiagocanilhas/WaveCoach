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
import waveCoach.http.model.input.AthleteCreateCharacteristicsInputModel
import waveCoach.http.model.input.AthleteUpdateCharacteristicsInputModel
import waveCoach.http.model.input.AthleteInputModel
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
        @RequestBody input: AthleteInputModel
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

    @GetMapping(Uris.Athletes.GET_BY_ID)
    fun getById(
        coach: AuthenticatedUser,
        @PathVariable aid: String
    ): ResponseEntity<*> {
        val uid = aid.toIntOrNull() ?: return Problem.response(400, Problem.invalidAthleteId)
        val result = athleteServices.getAthlete(coach.info.id, uid)
        return when (result) {
            is Success -> ResponseEntity.status(200).body(
                AthleteOutputModel(
                    result.value.uid,
                    result.value.coach,
                    result.value.name,
                    result.value.birthDate
                )
            )

            is Failure -> when (result.value) {
                GetAthleteError.AthleteNotFound -> Problem.response(404, Problem.athleteNotFound)
                GetAthleteError.NotAthletesCoach -> Problem.response(403, Problem.notAthletesCoach)
            }
        }
    }

    @GetMapping(Uris.Athletes.GET_BY_COACH)
    fun getByCoach(
        coach: AuthenticatedUser,
    ): ResponseEntity<*> {
        val result = athleteServices.getAthletes(coach.info.id)
        return ResponseEntity.status(200).body(AthleteListOutputModel(result.map {
            AthleteOutputModel(
                it.uid,
                it.coach,
                it.name,
                it.birthDate
            )
        }))
    }

    @PutMapping(Uris.Athletes.UPDATE)
    fun update(
        coach: AuthenticatedUser,
        @PathVariable aid: String,
        @RequestBody input: AthleteInputModel
    ): ResponseEntity<*> {
        val uid = aid.toIntOrNull() ?: return Problem.response(400, Problem.invalidAthleteId)
        val result = athleteServices.updateAthlete(coach.info.id, uid, input.name, input.birthDate)
        return when (result) {
            is Success -> ResponseEntity.status(204).build<Unit>()
            is Failure -> when (result.value) {
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
        val result = athleteServices.createCharacteristics(
            coach.info.id, uid, input.date, input.height, input.weight, input.calories,
            input.waist, input.arm, input.thigh, input.tricep, input.abdominal
        )

        return when (result) {
            is Success -> ResponseEntity
                .status(201)
                .header("Location", Uris.Athletes.characteristicsByDate(uid, result.value).toASCIIString())
                .build<Unit>()

            is Failure -> when (result.value) {
                CreateCharacteristicsError.AthleteNotFound -> Problem.response(404, Problem.athleteNotFound)
                CreateCharacteristicsError.CharacteristicsAlreadyExists -> Problem.response(
                    409,
                    Problem.characteristicsAlreadyExists
                )

                CreateCharacteristicsError.InvalidCharacteristics -> Problem.response(
                    400,
                    Problem.invalidCharacteristics
                )

                CreateCharacteristicsError.InvalidDate -> Problem.response(400, Problem.invalidDate)
                CreateCharacteristicsError.NotAthletesCoach -> Problem.response(403, Problem.notAthletesCoach)
            }
        }
    }

    @GetMapping(Uris.Athletes.GET_CHARACTERISTICS)
    fun getCharacteristics(
        coach: AuthenticatedUser,
        @PathVariable aid: String,
        @PathVariable date: String
    ): ResponseEntity<*> {
        val uid = aid.toIntOrNull() ?: return Problem.response(400, Problem.invalidAthleteId)
        val result = athleteServices.getCharacteristics(coach.info.id, uid, date)

        return when (result) {
            is Success -> ResponseEntity.status(200)
                .body(
                    CharacteristicsOutputModel(
                        result.value.date,
                        result.value.uid,
                        result.value.height,
                        result.value.weight,
                        result.value.calories,
                        result.value.waist,
                        result.value.arm,
                        result.value.thigh,
                        result.value.tricep,
                        result.value.abdominal
                    )
                )

            is Failure -> when (result.value) {
                GetCharacteristicsError.InvalidDate -> Problem.response(400, Problem.invalidDate)
                GetCharacteristicsError.AthleteNotFound -> Problem.response(404, Problem.athleteNotFound)
                GetCharacteristicsError.CharacteristicsNotFound -> Problem.response(
                    404,
                    Problem.characteristicsNotFound
                )

                GetCharacteristicsError.NotAthletesCoach -> Problem.response(403, Problem.notAthletesCoach)
            }
        }
    }

    @GetMapping(Uris.Athletes.GET_CHARACTERISTICS_LIST)
    fun getCharacteristicsList(
        coach: AuthenticatedUser,
        @PathVariable aid: String
    ): ResponseEntity<*> {
        val uid = aid.toIntOrNull() ?: return Problem.response(400, Problem.invalidAthleteId)
        val result = athleteServices.getCharacteristicsList(coach.info.id, uid)

        return when (result) {
            is Success -> ResponseEntity.status(200)
                .body(
                    CharacteristicsListOutputModel(
                        result.value.map {
                            CharacteristicsOutputModel(
                                it.date,
                                it.uid,
                                it.height,
                                it.weight,
                                it.calories,
                                it.waist,
                                it.arm,
                                it.thigh,
                                it.tricep,
                                it.abdominal
                            )
                        }
                    )
                )

            is Failure -> when (result.value) {
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
        @RequestBody input: AthleteUpdateCharacteristicsInputModel
    ): ResponseEntity<*> {
        val uid = aid.toIntOrNull() ?: return Problem.response(400, Problem.invalidAthleteId)
        val result = athleteServices.updateCharacteristics(
            coach.info.id, uid, date, input.height, input.weight, input.calories,
            input.waist, input.arm, input.thigh, input.tricep, input.abdominal
        )

        return when (result) {
            is Success -> ResponseEntity.status(204).build<Unit>()

            is Failure -> when (result.value) {
                UpdateCharacteristicsError.AthleteNotFound -> Problem.response(404, Problem.athleteNotFound)
                UpdateCharacteristicsError.InvalidCharacteristics -> Problem.response(
                    400,
                    Problem.invalidCharacteristics
                )

                UpdateCharacteristicsError.InvalidDate -> Problem.response(400, Problem.invalidDate)
                UpdateCharacteristicsError.NotAthletesCoach -> Problem.response(403, Problem.notAthletesCoach)
            }
        }
    }

    @DeleteMapping(Uris.Athletes.REMOVE_CHARACTERISTICS)
    fun removeCharacteristics(
        coach: AuthenticatedUser,
        @PathVariable aid: String,
        @PathVariable date: String
    ): ResponseEntity<*> {
        val uid = aid.toIntOrNull() ?: return Problem.response(400, Problem.invalidAthleteId)
        val result = athleteServices.removeCharacteristics(coach.info.id, uid, date)

        return when (result) {
            is Success -> ResponseEntity.status(204).build<Unit>()

            is Failure -> when (result.value) {
                RemoveCharacteristicsError.InvalidDate -> Problem.response(400, Problem.invalidDate)
                RemoveCharacteristicsError.AthleteNotFound -> Problem.response(404, Problem.athleteNotFound)
                RemoveCharacteristicsError.CharacteristicsNotFound -> Problem.response(
                    404,
                    Problem.characteristicsNotFound
                )

                RemoveCharacteristicsError.NotAthletesCoach -> Problem.response(403, Problem.notAthletesCoach)
            }
        }
    }
}
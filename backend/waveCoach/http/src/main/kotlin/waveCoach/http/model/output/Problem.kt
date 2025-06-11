package waveCoach.http.model.output

import org.springframework.http.ResponseEntity
import java.net.URI

class Problem(
    val title: String,
    typeUri: URI,
) {
    val type = typeUri.toASCIIString()

    companion object {
        const val MEDIA_TYPE = "application/problem+json"

        fun response(
            status: Int,
            problem: Problem,
        ) = ResponseEntity
            .status(status)
            .header("Content-Type", MEDIA_TYPE)
            .body<Any>(problem)

        private const val BASE_URI = ""

        val activityWithoutMicrocycle =
            Problem(
                "Activity without microcycle",
                URI.create("$BASE_URI/activity-without-microcycle"),
            )

        val credentialsAlreadyChanged =
            Problem(
                "Athlete credentials changed",
                URI.create("$BASE_URI/athlete-credentials-changed"),
            )

        val athleteNotFound =
            Problem(
                "Athlete not found",
                URI.create("$BASE_URI/athlete-not-found"),
            )

        val characteristicsAlreadyExists =
            Problem(
                "Characteristics already exists",
                URI.create("$BASE_URI/characteristics-already-exists"),
            )

        val characteristicsNotFound =
            Problem(
                "Characteristics not found",
                URI.create("$BASE_URI/characteristics-not-found"),
            )

        val insecurePassword =
            Problem(
                "Insecure password",
                URI("$BASE_URI/insecure-password"),
            )

        val invalidAthleteId =
            Problem(
                "Invalid athlete id",
                URI.create("$BASE_URI/invalid-athlete-id"),
            )

        val invalidBirthdate =
            Problem(
                "Invalid birth date",
                URI.create("$BASE_URI/invalid-birth-date"),
            )

        val invalidCharacteristics =
            Problem(
                "Invalid characteristics",
                URI.create("$BASE_URI/invalid-characteristics"),
            )

        val invalidCode =
            Problem(
                "Invalid code",
                URI("$BASE_URI/invalid-code"),
            )

        val invalidDate =
            Problem(
                "Invalid date",
                URI.create("$BASE_URI/invalid-date"),
            )

        val invalidLogin =
            Problem(
                "Invalid login",
                URI("$BASE_URI/invalid-login"),
            )

        val invalidName =
            Problem(
                "Invalid name",
                URI.create("$BASE_URI/invalid-name"),
            )

        val invalidPhoto =
            Problem(
                "Invalid photo",
                URI("$BASE_URI/invalid-photo"),
            )

        val invalidToken =
            Problem(
                "Invalid token",
                URI("$BASE_URI/invalid-token"),
            )

        val invalidUsername =
            Problem(
                "Invalid username",
                URI("$BASE_URI/invalid-username"),
            )

        val notAthletesCoach =
            Problem(
                "Not athlete's coach",
                URI.create("$BASE_URI/not-athletes-coach"),
            )

        val passwordIsBlank =
            Problem(
                "Password is blank",
                URI("$BASE_URI/password-is-blank"),
            )

        val questionnaireAlreadyExists =
            Problem(
                "Questionnaire already exists",
                URI("$BASE_URI/questionnaire-already-exists"),
            )

        val tokenNotFound =
            Problem(
                "Token not found",
                URI("$BASE_URI/token-not-found"),
            )

        val usernameAlreadyExists =
            Problem(
                "Username already exists",
                URI("$BASE_URI/username-already-exists"),
            )

        val usernameIsBlank =
            Problem(
                "Username is blank",
                URI("$BASE_URI/username-is-blank"),
            )

        val userIsNotACoach =
            Problem(
                "User is not a coach",
                URI("$BASE_URI/user-is-not-a-coach"),
            )

        val invalidGymActivityId =
            Problem(
                "Invalid activity id",
                URI("$BASE_URI/invalid-activity-id"),
            )

        val gymActivityNotFound =
            Problem(
                "Activity not found",
                URI("$BASE_URI/activity-not-found"),
            )

        val notGymActivity =
            Problem(
                "Not a gym activity",
                URI("$BASE_URI/not-gym-activity"),
            )

        val notAthletesActivity =
            Problem(
                "Not athlete's activity",
                URI("$BASE_URI/not-athletes-activity"),
            )

        val invalidGymExercise =
            Problem(
                "Invalid gym exercise",
                URI("$BASE_URI/invalid-gym-exercise"),
            )

        val gymExerciseNotFound =
            Problem(
                "Gym exercise not found",
                URI("$BASE_URI/gym-exercise-not-found"),
            )

        val nameAlreadyExists =
            Problem(
                "Name already exists",
                URI("$BASE_URI/name-already-exists"),
            )

        val invalidCategory =
            Problem(
                "Invalid category",
                URI("$BASE_URI/invalid-category"),
            )

        val invalidSet =
            Problem(
                "Invalid set",
                URI("$BASE_URI/invalid-set"),
            )

        val invalidRpe =
            Problem(
                "Invalid RPE",
                URI("$BASE_URI/invalid-rpe"),
            )

        val invalidTrimp =
            Problem(
                "Invalid TRIMP",
                URI("$BASE_URI/invalid-trimp"),
            )

        val invalidDuration =
            Problem(
                "Invalid duration",
                URI("$BASE_URI/invalid-duration"),
            )

        val invalidWaterManeuver =
            Problem(
                "Invalid maneuver",
                URI("$BASE_URI/invalid-maneuver"),
            )

        val invalidWaterActivityId =
            Problem(
                "Invalid water activity id",
                URI("$BASE_URI/invalid-water-activity-id"),
            )

        val invalidSleep =
            Problem(
                "Invalid sleep value",
                URI("$BASE_URI/invalid-sleep"),
            )

        val invalidFatigue =
            Problem(
                "Invalid fatigue value",
                URI("$BASE_URI/invalid-fatigue"),
            )

        val invalidStress =
            Problem(
                "Invalid stress value",
                URI("$BASE_URI/invalid-stress"),
            )

        val invalidMusclePain =
            Problem(
                "Invalid muscle pain value",
                URI("$BASE_URI/invalid-muscle-pain"),
            )

        val waterActivityNotFound =
            Problem(
                "Water activity not found",
                URI("$BASE_URI/water-activity-not-found"),
            )

        val notWaterActivity =
            Problem(
                "Not a water activity",
                URI("$BASE_URI/not-water-activity"),
            )

        val questionnaireNotFound =
            Problem(
                "Questionnaire not found",
                URI("$BASE_URI/questionnaire-not-found"),
            )

        val invalidExerciseId =
            Problem(
                "Invalid exercise id",
                URI("$BASE_URI/invalid-exercise-id"),
            )

        val notActivityExercise =
            Problem(
                "Not activity's exercise",
                URI("$BASE_URI/not-activity-exercise"),
            )

        val invalidOrder =
            Problem(
                "Invalid order",
                URI("$BASE_URI/invalid-order"),
            )

        val invalidSetId =
            Problem(
                "Invalid set id",
                URI("$BASE_URI/invalid-set-id"),
            )

        val setNotFound =
            Problem(
                "Set not found",
                URI("$BASE_URI/set-not-found"),
            )

        val notExerciseSet =
            Problem(
                "Not exercise's set",
                URI("$BASE_URI/not-exercise-set"),
            )

        val exerciseNotFound =
            Problem(
                "Exercise not found",
                URI("$BASE_URI/exercise-not-found"),
            )

        val invalidWaveId =
            Problem(
                "Invalid wave id",
                URI("$BASE_URI/invalid-wave-id"),
            )

        val waveNotFound =
            Problem(
                "Wave not found",
                URI("$BASE_URI/wave-not-found"),
            )

        val notActivityWave =
            Problem(
                "Not activity's wave",
                URI("$BASE_URI/not-activity-wave"),
            )

        val invalidManeuverId =
            Problem(
                "Invalid maneuver id",
                URI("$BASE_URI/invalid-maneuver-id"),
            )

        val maneuverNotFound =
            Problem(
                "Maneuver not found",
                URI("$BASE_URI/maneuver-not-found"),
            )

        val notWaveManeuver =
            Problem(
                "Not wave's maneuver",
                URI("$BASE_URI/not-wave-maneuver"),
            )
    }
}

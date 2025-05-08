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

        val invalidBirthDate =
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

        val invalidSets =
            Problem(
                "Invalid set",
                URI("$BASE_URI/invalid-set"),
            )
    }
}

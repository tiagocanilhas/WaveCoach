package waveCoach.domain

import kotlinx.datetime.Instant
import org.springframework.stereotype.Component
import kotlin.math.abs
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

@Component
class AthleteDomain(
    private val tokenEncoder: TokenEncoder,
) {
    fun isNameValid(name: String) = name.length in 1..64

    fun createAthleteUsername() = "Athlete_${abs(Random.nextLong())}"

    val athleteDefaultPassword = "changeit"

    fun createAthleteCodeValidationInformation(code: String): CodeValidationInfo = tokenEncoder.createCodeValidationInformation(code)

    private val codeExpirationTime: Duration = 7.days

    fun getCodeExpiration(time: Instant): Instant = time + codeExpirationTime
}

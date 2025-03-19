package waveCoach.domain

import org.springframework.stereotype.Component
import kotlin.math.abs
import kotlin.random.Random


@Component
class AthleteDomain {
    fun isNameValid(name: String) = name.length in 1..64

    fun createAthleteUsername() = "Athlete_${abs(Random.nextLong())}"

    val athleteDefaultPassword = "changeit"
}
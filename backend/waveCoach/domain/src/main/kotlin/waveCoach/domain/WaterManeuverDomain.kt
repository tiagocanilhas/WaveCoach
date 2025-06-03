package waveCoach.domain

import org.springframework.stereotype.Component

@Component
class WaterManeuverDomain {
    fun isNameValid(name: String) = name.length in 1..64
}

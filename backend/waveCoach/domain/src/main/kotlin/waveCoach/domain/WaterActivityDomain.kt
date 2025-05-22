package waveCoach.domain

import org.springframework.stereotype.Component

@Component
class WaterActivityDomain {
    fun checkPse(pse: Int): Boolean {
        return pse in 1..10
    }

    fun checkHeartRate(heartRate: Int): Boolean {
        return heartRate in 1..200
    }

    fun checkDuration(duration: Int): Boolean {
        return duration > 0
    }
}
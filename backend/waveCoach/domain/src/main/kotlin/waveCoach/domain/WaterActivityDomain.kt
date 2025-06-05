package waveCoach.domain

import org.springframework.stereotype.Component

@Component
class WaterActivityDomain {
    fun checkRpe(rpe: Int) = rpe in 1..10
    fun checkTrimp(trimp: Int) = trimp in 1..200
    fun checkDuration(duration: Int) = duration > 0

    fun checkQuestionnaireValue(value: Int) = value in 1..5
}

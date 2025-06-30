package waveCoach.domain

import org.springframework.stereotype.Component

@Component
class SetsDomain {
    fun checkSet(
        reps: Int?,
        weight: Float?,
        restTime: Float?
    ): Boolean =
        (reps ?: 0) >= 0 && (weight ?: 0f) >= 0 && (restTime ?: 0f) >= 0

    /*private fun checkSet(sets: UpdateSetInputInfo): Boolean {
        return (sets.reps ?: 0) > 0 && (sets.weight ?: 0f) > 0 && (sets.rest ?: 0f) > 0
    }*/
}

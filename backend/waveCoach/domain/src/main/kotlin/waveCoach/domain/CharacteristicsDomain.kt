package waveCoach.domain

import org.springframework.stereotype.Component
import java.text.SimpleDateFormat
import java.util.Locale

@Component
class CharacteristicsDomain {
    fun checkCharacteristics(
        height: Int?,
        weight: Float?,
        calories: Int?,
        waist: Int?,
        arm: Int?,
        thigh: Int?,
        tricep: Float?,
        abdominal: Float?
    ): Boolean {
        return height != null && height >= 0 &&
                weight != null && weight >= 0 &&
                calories != null && calories >= 0 &&
                waist != null && waist >= 0 &&
                arm != null && arm >= 0 &&
                thigh != null && thigh >= 0 &&
                tricep != null && tricep >= 0 &&
                abdominal != null && abdominal >= 0
    }
}
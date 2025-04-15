package waveCoach.domain

import org.springframework.stereotype.Component

@Component
class CharacteristicsDomain {
    fun checkCharacteristics(vararg characteristics: Number?) = characteristics.all { it == null || it.toDouble() >= 0 }
}

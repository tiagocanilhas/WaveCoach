package waveCoach.domain

import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException


@Component
class AthleteDomain {
    fun birthDateValid(birthDate: String): Boolean {
        return try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val date = LocalDate.parse(birthDate, formatter)
            date.isBefore(LocalDate.now())
        } catch (e: DateTimeParseException) {
            false
        }
    }

    fun nameValid(name: String): Boolean {
        return name.length in 1..64
    }
}
package waveCoach.domain

import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException


@Component
class AthleteDomain {
    fun birthDateToLong(birthDate: String): Long? {
        return try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val date = LocalDate.parse(birthDate, formatter)

            if (date.isBefore(LocalDate.now())) {
                date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            } else {
                null
            }
        } catch (e: DateTimeParseException) {
            null
        }
    }

    fun nameValid(name: String): Boolean {
        return name.length in 1..64
    }
}
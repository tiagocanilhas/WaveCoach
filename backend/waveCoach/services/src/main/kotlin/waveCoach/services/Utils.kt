package waveCoach.services

import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

fun dateToLong(date: String): Long? {
    return try {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val dateParsed = LocalDate.parse(date, formatter)

        dateParsed.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    } catch (e: DateTimeParseException) {
        null
    }
}

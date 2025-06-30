package waveCoach.services

import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import kotlin.reflect.full.memberProperties

fun dateToLong(date: String): Long? {
    return try {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val dateParsed = LocalDate.parse(date, formatter)

        dateParsed.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    } catch (e: DateTimeParseException) {
        null
    }
}

fun <T : Any> separateCreateUpdateDelete(list: List<T>): Triple<List<T>, List<T>, List<Int>> {
    val creates = mutableListOf<T>()
    val updates = mutableListOf<T>()
    val deletes = mutableListOf<Int>()

    list.forEach { item ->
        val id = item::class.memberProperties
            .find { it.name == "id" }
            ?.getter
            ?.call(item) as? Int?

        val otherValues = item::class.memberProperties
            .filterNot { it.name == "id" }
            .map { it.getter.call(item) }

        when {
            id == null -> creates.add(item)
            id > 0 && otherValues.all { it == null } -> deletes.add(id)
            else -> updates.add(item)
        }
    }

    return Triple(creates, updates, deletes)
}

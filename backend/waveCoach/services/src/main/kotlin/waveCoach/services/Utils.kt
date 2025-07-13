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

/*fun <T : Any> separateCreateUpdateDelete(list: List<T>): Triple<List<T>, List<T>, List<Int>> {
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
}*/

fun <T : Any> separateCreateUpdateDelete(list: List<T>): Triple<List<T>, List<T>, List<Int>> {
    val creates = mutableListOf<T>()
    val updates = mutableListOf<T>()
    val deletes = mutableListOf<Int>()

    list.forEach { item ->
        val id =
            item::class.memberProperties
                .find { it.name == "id" }
                ?.getter
                ?.call(item) as? Int?

        val otherValues =
            item::class.memberProperties
                .filterNot { it.name == "id" }
                .map { it.getter.call(item) }

        val allNullRecursively = otherValues.all { isCompletelyNull(it) }

        when {
            id == null -> creates.add(item)
            id > 0 && allNullRecursively -> deletes.add(id)
            else -> updates.add(item)
        }
    }

    return Triple(creates, updates, deletes)
}

fun isCompletelyNull(value: Any?): Boolean {
    if (value == null) return true

    // Se for uma lista, todos os elementos têm que ser null (recursivamente)
    if (value is List<*>) {
        return value.all { isCompletelyNull(it) }
    }

    // Se for uma data class, verificar os campos dela
    val kClass = value::class
    if (kClass.isData) {
        val properties = kClass.memberProperties
        return properties.all { isCompletelyNull(it.getter.call(value)) }
    }

    // Qualquer outro valor consideramos não nulo (ex: String vazia, 0, etc.)
    return false
}

fun <T : Any, U : Any> checkOrderConflict(
    itemsOnDB: List<T>,
    itemsToUpdate: List<U>,
    orderPropOnDB: String,
    order: Int,
): Boolean {
    val conflictDBItem =
        itemsOnDB.find { dbItem ->
            val dbOrder = dbItem::class.memberProperties.find { prop -> prop.name == orderPropOnDB }?.getter?.call(dbItem)

            dbOrder != null && dbOrder == order
        }

    if (conflictDBItem != null) {
        val dbItemId =
            conflictDBItem::class.memberProperties.find { prop -> prop.name == "id" }?.getter?.call(conflictDBItem)

        val updateItem =
            itemsToUpdate.find { updateItem ->
                val updateItemId =
                    updateItem::class.memberProperties.find { prop -> prop.name == "id" }?.getter?.call(updateItem)

                updateItemId != null && updateItemId == dbItemId
            }
        if (updateItem == null) return false

        val otherValues =
            updateItem::class.memberProperties
                .filterNot { it.name == "id" }
                .map { it.getter.call(updateItem) }

        if (otherValues.all { isCompletelyNull(it) }) {
            return true
        }

        val updateItemOrder =
            updateItem::class.memberProperties.find { prop -> prop.name == "order" }?.getter?.call(updateItem) as? Int?

        val conflictItemOrder =
            conflictDBItem::class.memberProperties.find { prop -> prop.name == orderPropOnDB }?.getter?.call(conflictDBItem) as? Int?

        if (updateItemOrder == null || updateItemOrder <= 0 || updateItemOrder == conflictItemOrder) {
            return false
        }
    }
    return true
}
/*
 val conflictOrderWave = wavesOnDB.find { w -> w.waveOrder == wave.order }

                    if (conflictOrderWave != null) {
                        val w = waves.find { w -> w.id == conflictOrderWave.id }

                        if (w != null && (w.order == null || w.order <= 0 || w.order == conflictOrderWave.waveOrder))
                            return@run failure(UpdateWaterActivityError.InvalidWaveOrder)
                    }
 */

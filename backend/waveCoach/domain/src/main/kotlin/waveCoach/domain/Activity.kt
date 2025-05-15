package waveCoach.domain

enum class ActivityType {
    GYM, WATER;

    override fun toString(): String = name.lowercase()

    companion object {
        fun fromString(value: String): ActivityType? =
            entries.firstOrNull { it.name.equals(value, ignoreCase = true) }
    }
}

data class Activity(
    val id: Int,
    val uid: Int,
    val microcycle: Int,
    val date: Long,
    val type: ActivityType?,
)

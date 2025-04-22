package waveCoach.domain

enum class ActivityType {
    GYM, WATER;

    override fun toString(): String = name.lowercase()
}

data class Activity(
    val id: Int,
    val uid: Int,
    val date: Long,
    val type: ActivityType?,
)

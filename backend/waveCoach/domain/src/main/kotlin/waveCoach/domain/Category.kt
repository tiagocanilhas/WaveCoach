package waveCoach.domain

enum class Category {
    CHEST, BACK, LEGS, ARMS, SHOULDERS;

    override fun toString(): String = name.lowercase()

    companion object {
        fun toCategory(value: String): Category? = entries.find { it.name.equals(value.uppercase(), ignoreCase = true) }
    }
}
package waveCoach.domain

import org.springframework.stereotype.Component

@Component
class ActivityDomain {
    fun isValidType(type: String): ActivityType? = ActivityType.fromString(type)

    fun areDatesValid(
        startDate: Long,
        endDate: Long,
    ): Boolean {
        return startDate in 1..endDate
    }

    fun compareCycles(
        startTimeDb: Long?,
        endTimeDb: Long?,
        startTimeNew: Long,
        endTimeNew: Long,
    ): Boolean {
        if (startTimeDb == null || endTimeDb == null) return false
        return startTimeDb == startTimeNew && endTimeDb == endTimeNew
    }
}

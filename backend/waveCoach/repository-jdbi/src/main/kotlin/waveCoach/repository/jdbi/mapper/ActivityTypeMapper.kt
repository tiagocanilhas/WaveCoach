package waveCoach.repository.jdbi.mapper

import org.jdbi.v3.core.mapper.ColumnMapper
import org.jdbi.v3.core.statement.StatementContext
import waveCoach.domain.ActivityType
import java.sql.ResultSet
import java.sql.SQLException
import kotlin.jvm.Throws

class ActivityTypeMapper : ColumnMapper<ActivityType> {
    @Throws(SQLException::class)
    override fun map(
        r: ResultSet,
        columnNumber: Int,
        ctx: StatementContext,
    ): ActivityType {
        return ActivityType.valueOf(r.getString(columnNumber))
    }
}

package waveCoach.repository.jdbi.mapper

import org.jdbi.v3.core.mapper.ColumnMapper
import org.jdbi.v3.core.statement.StatementContext
import waveCoach.domain.Category
import java.sql.ResultSet
import java.sql.SQLException

class CategoryMapper : ColumnMapper<Category> {
    @Throws(SQLException::class)
    override fun map(
        r: ResultSet,
        columnNumber: Int,
        ctx: StatementContext,
    ): Category {
        return Category.valueOf(r.getString(columnNumber))
    }
}

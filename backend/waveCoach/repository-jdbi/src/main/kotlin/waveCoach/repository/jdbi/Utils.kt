package waveCoach.repository.jdbi

import kotlinx.datetime.Instant
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.postgres.PostgresPlugin
import waveCoach.domain.PasswordValidationInfo
import waveCoach.domain.TokenValidationInfo
import waveCoach.repository.jdbi.mapper.*

fun Jdbi.configureWithAppRequirements(): Jdbi {
    installPlugin(KotlinPlugin())
    installPlugin(PostgresPlugin())

    registerColumnMapper(ActivityTypeMapper::class.java, ActivityTypeMapper())
    registerColumnMapper(CategoryMapper::class.java, CategoryMapper())
    registerColumnMapper(Instant::class.java, InstantMapper())
    registerColumnMapper(PasswordValidationInfo::class.java, PasswordValidationInfoMapper())
    registerColumnMapper(TokenValidationInfo::class.java, TokenValidationInfoMapper())

    return this
}

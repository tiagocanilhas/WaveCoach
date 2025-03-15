package waveCoach.repository.jdbi

import kotlinx.datetime.Instant
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.postgres.PostgresPlugin
import waveCoach.domain.PasswordValidationInfo
import waveCoach.domain.TokenValidationInfo
import waveCoach.repository.jdbi.mapper.InstantMapper
import waveCoach.repository.jdbi.mapper.PasswordValidationInfoMapper
import waveCoach.repository.jdbi.mapper.TokenValidationInfoMapper

fun Jdbi.configureWithAppRequirements(): Jdbi {
    installPlugin(KotlinPlugin())
    installPlugin(PostgresPlugin())

    registerColumnMapper(Instant::class.java, InstantMapper())
    registerColumnMapper(PasswordValidationInfo::class.java, PasswordValidationInfoMapper())
    registerColumnMapper(TokenValidationInfo::class.java, TokenValidationInfoMapper())

    return this
}
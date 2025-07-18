package waveCoach.host

import com.cloudinary.Cloudinary
import kotlinx.datetime.Clock
import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import waveCoach.domain.Sha256TokenEncoder
import waveCoach.domain.UserDomainConfig
import waveCoach.http.pipeline.AuthenticatedCoachArgumentResolver
import waveCoach.http.pipeline.AuthenticatedUserArgumentResolver
import waveCoach.http.pipeline.AuthenticationInterceptor
import waveCoach.repository.jdbi.configureWithAppRequirements
import kotlin.time.Duration.Companion.hours

@SpringBootApplication
@ComponentScan("waveCoach")
class WaveCoachApplication {
    @Bean
    fun jdbi() =
        Jdbi.create(
            PGSimpleDataSource().apply {
                setURL(Environment.getDbUrl())
            },
        ).configureWithAppRequirements()

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun tokenEncoder() = Sha256TokenEncoder()

    @Bean
    fun clock() = Clock.System

    @Bean
    fun userDomainConfig() =
        UserDomainConfig(
            tokenSizeInBytes = 256 / 8,
            tokenTtl = 24.hours,
            tokenRollingTtl = 1.hours,
            maxTokensPerUser = 3,
        )

    @Bean
    fun cloudinary(): Cloudinary =
        Cloudinary(
            mapOf(
                "cloud_name" to Environment.getCloudName(),
                "api_key" to Environment.getApiKey(),
                "api_secret" to Environment.getApiSecret(),
            ),
        )
}

@Configuration
class PipelineConfigurer(
    val authenticationInterceptor: AuthenticationInterceptor,
    val authenticatedUserArgumentResolver: AuthenticatedUserArgumentResolver,
    val authenticatedCoachArgumentResolver: AuthenticatedCoachArgumentResolver,
) : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(authenticationInterceptor)
    }

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(authenticatedUserArgumentResolver)
        resolvers.add(authenticatedCoachArgumentResolver)
    }
}

fun main(args: Array<String>) {
    runApplication<WaveCoachApplication>(*args)
}

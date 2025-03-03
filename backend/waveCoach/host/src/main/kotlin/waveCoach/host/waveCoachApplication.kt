package waveCoach.host

import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import waveCoach.repository.jdbi.configureWithAppRequirements

@SpringBootApplication
class ImSystemApplication {
    @Bean
    fun jdbi() = Jdbi.create(
        PGSimpleDataSource().apply {
            setURL(Environment.getDbUrl())
        },
    ).configureWithAppRequirements()
}


fun main(args: Array<String>) {
    runApplication<ImSystemApplication>(*args)
}
package waveCoach.repository.jdbi

import org.jdbi.v3.core.Handle
import waveCoach.repository.AthleteRepository
import waveCoach.repository.Transaction
import waveCoach.repository.UserRepository

class JdbiTransaction(
    private val handle: Handle,
) : Transaction {
    override val userRepository: UserRepository = JdbiUserRepository(handle)
    override val athleteRepository: AthleteRepository = JdbiAthleteRepository(handle)

    override fun rollback() {
        handle.rollback()
    }
}
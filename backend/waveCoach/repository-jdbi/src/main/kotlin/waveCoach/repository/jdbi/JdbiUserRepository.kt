package waveCoach.repository.jdbi

import org.jdbi.v3.core.Handle
import waveCoach.repository.UserRepository

class JdbiUserRepository(
    private val handle: Handle,
) : UserRepository {
    // TODO: Implement JdbiUserRepository
}
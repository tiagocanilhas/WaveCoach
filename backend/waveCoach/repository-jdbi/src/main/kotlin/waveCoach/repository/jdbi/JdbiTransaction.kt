package waveCoach.repository.jdbi

import org.jdbi.v3.core.Handle
import waveCoach.repository.ActivityRepository
import waveCoach.repository.AthleteRepository
import waveCoach.repository.CharacteristicsRepository
import waveCoach.repository.CoachRepository
import waveCoach.repository.GymActivityRepository
import waveCoach.repository.Transaction
import waveCoach.repository.UserRepository
import waveCoach.repository.WaterManeuverRepository

class JdbiTransaction(
    private val handle: Handle,
) : Transaction {
    override val userRepository: UserRepository = JdbiUserRepository(handle)
    override val coachRepository: CoachRepository = JdbiCoachRepository(handle)
    override val athleteRepository: AthleteRepository = JdbiAthleteRepository(handle)
    override val characteristicsRepository: CharacteristicsRepository = JdbiCharacteristicsRepository(handle)
    override val activityRepository: ActivityRepository = JdbiActivityRepository(handle)
    override val gymActivityRepository: GymActivityRepository = JdbiGymActivityRepository(handle)
    override val waterActivityRepository: waveCoach.repository.WaterActivityRepository =
        JdbiWaterActivityRepository(handle)
    override val waterManeuverRepository: WaterManeuverRepository = JdbiWaterManeuverRepository(handle)

    override fun rollback() {
        handle.rollback()
    }
}

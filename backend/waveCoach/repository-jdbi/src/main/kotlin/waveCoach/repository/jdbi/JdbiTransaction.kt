package waveCoach.repository.jdbi

import org.jdbi.v3.core.Handle
import waveCoach.repository.*

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
    override val competitionRepository: CompetitionRepository = JdbiCompetitionRepository(handle)

    override fun rollback() {
        handle.rollback()
    }
}

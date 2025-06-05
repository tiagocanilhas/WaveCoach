package waveCoach.repository

interface Transaction {
    val userRepository: UserRepository
    val coachRepository: CoachRepository
    val athleteRepository: AthleteRepository
    val characteristicsRepository: CharacteristicsRepository
    val activityRepository: ActivityRepository
    val gymActivityRepository: GymActivityRepository
    val waterActivityRepository: WaterActivityRepository
    val waterManeuverRepository: WaterManeuverRepository
    val competitionRepository: CompetitionRepository

    // other repository types
    fun rollback()
}

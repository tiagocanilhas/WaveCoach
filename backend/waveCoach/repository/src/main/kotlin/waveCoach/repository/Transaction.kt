package waveCoach.repository

interface Transaction {
    val userRepository: UserRepository
    val coachRepository: CoachRepository
    val athleteRepository: AthleteRepository
    val characteristicsRepository: CharacteristicsRepository
    val activityRepository: ActivityRepository
    val gymActivityRepository: GymActivityRepository
    val waterManeuverRepository: WaterManeuverRepository

    // other repository types
    fun rollback()
}

package waveCoach.repository

import waveCoach.repository.UserRepository

interface Transaction {
    val userRepository: UserRepository
    val athleteRepository: AthleteRepository
    val characteristicsRepository: CharacteristicsRepository
    val activityRepository: ActivityRepository
    val gymActivityRepository: GymActivityRepository
    // other repository types
    fun rollback()
}
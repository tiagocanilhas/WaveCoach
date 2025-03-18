package waveCoach.repository

import waveCoach.repository.UserRepository

interface Transaction {
    val userRepository: UserRepository
    val athleteRepository: AthleteRepository
    val characteristicsRepository: CharacteristicsRepository
    // other repository types
    fun rollback()
}
package waveCoach.repository

import waveCoach.repository.UserRepository

interface Transaction {
    val userRepository: UserRepository
    val athleteRepository: AthleteRepository
    // other repository types
    fun rollback()
}
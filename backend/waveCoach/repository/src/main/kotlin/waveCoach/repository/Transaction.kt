package waveCoach.repository

import waveCoach.repository.UserRepository

interface Transaction {
    val userRepository: UserRepository

    // other repository types
    fun rollback()
}
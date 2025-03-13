package waveCoach.repository

import kotlinx.datetime.Instant
import waveCoach.domain.PasswordValidationInfo
import waveCoach.domain.Token
import waveCoach.domain.TokenValidationInfo
import waveCoach.domain.User

interface UserRepository {
    fun storeUser(username: String, passwordValidationInfo: PasswordValidationInfo): Int

    fun checkUsername(username: String): Boolean

    fun storeToken(token: Token, maxTokens: Int)

    fun getToken(tokenValidationInfo: TokenValidationInfo): Pair<User, Token>?

    fun updateTokenLastUsed(token: Token, now: Instant)

    fun removeToken(tokenValidationInfo: TokenValidationInfo)
}
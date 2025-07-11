package waveCoach.repository

import kotlinx.datetime.Instant
import waveCoach.domain.PasswordValidationInfo
import waveCoach.domain.Token
import waveCoach.domain.TokenValidationInfo
import waveCoach.domain.User

interface UserRepository {
    // User methods
    fun storeUser(
        username: String,
        passwordValidationInfo: PasswordValidationInfo,
    ): Int

    fun getUserById(uid: Int): User?

    fun getUserByUsername(username: String): User?

    fun checkUsername(username: String): Boolean

    fun storeToken(
        token: Token,
        maxTokens: Int,
    )

    fun updateUser(
        uid: Int,
        username: String,
        passwordValidationInfo: PasswordValidationInfo,
    )

    fun updateUsername(
        uid: Int,
        username: String,
    )

    fun updatePassword(
        uid: Int,
        passwordValidationInfo: PasswordValidationInfo,
    )

    fun removeUser(uid: Int)

    // Token methods
    fun getToken(tokenValidationInfo: TokenValidationInfo): Pair<User, Token>?

    fun updateTokenLastUsed(
        token: Token,
        now: Instant,
    )

    fun removeToken(tokenValidationInfo: TokenValidationInfo)

    fun removeTokensByUserId(userId: Int)
}

package waveCoach.repository.jdbi

import kotlinx.datetime.Instant
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import waveCoach.domain.PasswordValidationInfo
import waveCoach.domain.Token
import waveCoach.domain.TokenValidationInfo
import waveCoach.domain.User
import waveCoach.repository.UserRepository

class JdbiUserRepository(
    private val handle: Handle,
) : UserRepository {
    override fun storeUser(username: String, passwordValidationInfo: PasswordValidationInfo): Int =
        handle.createUpdate("insert into waveCoach.user (username, password) values (:username, :password)")
            .bind("username", username)
            .bind("password", passwordValidationInfo.value)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .one()

    override fun checkUsername(username: String): Boolean =
        handle.createQuery("select count(*) from waveCoach.user where username = :username")
            .bind("username", username)
            .mapTo<Int>()
            .single() == 1

    override fun storeToken(token: Token, maxTokens: Int) {
        TODO("Not yet implemented")
    }

    override fun getToken(tokenValidationInfo: TokenValidationInfo): Pair<User, Token>? {
        TODO("Not yet implemented")
    }

    override fun updateTokenLastUsed(token: Token, now: Instant) {
        TODO("Not yet implemented")
    }

    override fun removeToken(tokenValidationInfo: TokenValidationInfo) {
        TODO("Not yet implemented")
    }
}
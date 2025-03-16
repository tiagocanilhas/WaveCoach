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

    override fun getUserByUsername(username: String): User? =
        handle.createQuery("select * from waveCoach.user where username = :username")
            .bind("username", username)
            .mapTo<User>()
            .singleOrNull()

    override fun checkUsername(username: String): Boolean =
        handle.createQuery("select count(*) from waveCoach.user where username = :username")
            .bind("username", username)
            .mapTo<Int>()
            .single() == 1

    override fun storeToken(token: Token, maxTokens: Int) {
        handle.createUpdate(
            """
            delete from waveCoach.token 
            where uid = :uid and token in (
                select token from waveCoach.token where uid = :uid order by used_time desc offset :offset
            )
            """.trimIndent(),
        )
            .bind("uid", token.uid)
            .bind("offset", maxTokens - 1)
            .execute()

        handle.createUpdate(
            """
            insert into waveCoach.token (token, uid, created_time, used_time) 
            values (:token, :uid, :created_time, :used_time)
            """.trimIndent(),
        )
            .bind("token", token.tokenValidationInfo.value)
            .bind("uid", token.uid)
            .bind("created_time", token.createdTime.epochSeconds)
            .bind("used_time", token.usedTime.epochSeconds)
            .execute()
    }

    override fun getToken(tokenValidationInfo: TokenValidationInfo): Pair<User, Token>? =
        handle.createQuery(
            """
            select id, username, password, token, created_time, used_time from waveCoach.user 
            join waveCoach.token on id = uid
            where token = :token
            """.trimIndent(),
        )
            .bind("token", tokenValidationInfo.value)
            .mapTo<UserAndTokenModel>()
            .singleOrNull()
            ?.userAndToken

    private data class UserAndTokenModel(
        val id: Int,
        val username: String,
        val password: PasswordValidationInfo,
        val token: TokenValidationInfo,
        val createdTime: Long,
        val usedTime: Long,
    ) {
        val userAndToken: Pair<User, Token>
            get() = Pair(
                User(id, username, password),
                Token(token, id, Instant.fromEpochSeconds(createdTime), Instant.fromEpochSeconds(usedTime)),
            )
    }

    override fun updateTokenLastUsed(token: Token, now: Instant) {
        TODO("Not yet implemented")
    }

    override fun removeToken(tokenValidationInfo: TokenValidationInfo) {
        TODO("Not yet implemented")
    }
}
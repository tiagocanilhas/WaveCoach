package waveCoach.http

import org.springframework.web.util.UriTemplate
import java.net.URI

object Uris {
    const val PREFIX = "/api"

    fun home(): URI = URI(PREFIX)

    object Users {
        private const val USERS = "$PREFIX/users"

        const val LOGIN = "$PREFIX/login"
        const val LOGOUT = "$PREFIX/logout"

        const val CREATE = USERS
        const val GET_BY_ID = "$USERS/{uid}"

        fun byId(id: Int): URI = UriTemplate(GET_BY_ID).expand(id)
    }

    object Athletes {
        private const val ATHLETES = "$PREFIX/athletes"

        const val CREATE = ATHLETES
        const val REMOVE = "$ATHLETES/{aid}"
        const val GET_BY_ID = "$ATHLETES/{aid}"

        const val CREATE_CHARACTERISTICS = "$ATHLETES/{aid}/characteristics"

        fun byId(id: Int): URI = UriTemplate(GET_BY_ID).expand(id)
    }
}
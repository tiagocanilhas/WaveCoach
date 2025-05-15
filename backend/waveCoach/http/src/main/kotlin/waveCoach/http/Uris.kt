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

        const val AUTH_CHECK = "$PREFIX/me"
        const val UPDATE = "$PREFIX/me"

        const val GET_BY_ID = "$USERS/{uid}"

        fun byId(id: Int): URI = UriTemplate(GET_BY_ID).expand(id)
    }

    object Coaches {
        private const val COACHES = "$PREFIX/coaches"

        const val CREATE = COACHES
        const val GET_BY_ID = "$COACHES/{cid}"

        fun byId(id: Int): URI = UriTemplate(GET_BY_ID).expand(id)
    }

    object GymActivity {
        private const val GYM_ACTIVITY = "$PREFIX/gym"

        const val CREATE = GYM_ACTIVITY
        const val GET_BY_ID = "$GYM_ACTIVITY/{activityId}"
        const val UPDATE = "$GYM_ACTIVITY/{activityId}"
        const val REMOVE = "$GYM_ACTIVITY/{activityId}"

        fun byId(id: Int): URI = UriTemplate(GET_BY_ID).expand(id)
    }

    object GymExercise {
        private const val GYM_EXERCISE = "$PREFIX/gym/exercise"

        const val CREATE = GYM_EXERCISE
        const val GET_BY_ID = "$GYM_EXERCISE/{geid}"
        const val GET_ALL = GYM_EXERCISE
        const val UPDATE = "$GYM_EXERCISE/{geid}"
        const val REMOVE = "$GYM_EXERCISE/{geid}"

        fun byId(id: Int): URI = UriTemplate(GET_BY_ID).expand(id)
    }

    object Athletes {
        private const val ATHLETES = "$PREFIX/athletes"

        const val CREATE = ATHLETES
        const val GET_BY_ID = "$ATHLETES/{aid}"
        const val GET_BY_COACH = ATHLETES
        const val UPDATE = "$ATHLETES/{aid}"
        const val REMOVE = "$ATHLETES/{aid}"

        const val GENERATE_CODE = "$ATHLETES/{aid}/code"
        const val GET_BY_CODE = "$ATHLETES/code/{code}"
        const val CHANGE_CREDENTIALS = "$ATHLETES/credentials"

        const val CREATE_CHARACTERISTICS = "$ATHLETES/{aid}/characteristics"
        const val GET_CHARACTERISTICS = "$ATHLETES/{aid}/characteristics/{date}"
        const val GET_CHARACTERISTICS_LIST = "$ATHLETES/{aid}/characteristics"
        const val UPDATE_CHARACTERISTICS = "$ATHLETES/{aid}/characteristics/{date}"
        const val REMOVE_CHARACTERISTICS = "$ATHLETES/{aid}/characteristics/{date}"

        const val CREATE_WATER_ACTIVITY = "$ATHLETES/{aid}/water"

        const val CREATE_CALENDAR = "$ATHLETES/{aid}/calendar"
        const val GET_CALENDAR = "$ATHLETES/{aid}/calendar"
        const val GET_ACTIVITIES = "$ATHLETES/{aid}/activities"

        fun byId(id: Int): URI = UriTemplate(GET_BY_ID).expand(id)

        fun characteristicsByDate(
            aid: Int,
            date: Long,
        ): URI = UriTemplate(GET_CHARACTERISTICS).expand(aid, date)
    }
}

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

        const val ADD_EXERCISE = "$GYM_ACTIVITY/{activityId}/exercise"
        const val REMOVE_EXERCISE = "$GYM_ACTIVITY/{activityId}/exercise/{exerciseId}"

        fun exerciseById(activityId: Int, exerciseId: Int): URI =
            UriTemplate(REMOVE_EXERCISE).expand(activityId, exerciseId)

        const val ADD_SET = "$GYM_ACTIVITY/{activityId}/exercise/{exerciseId}/set"
        const val REMOVE_SET = "$GYM_ACTIVITY/{activityId}/exercise/{exerciseId}/set/{setId}"

        fun setById(activityId: Int, exerciseId: Int, setId: Int): URI =
            UriTemplate(REMOVE_SET).expand(activityId, exerciseId, setId)
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

    object WaterActivity {
        private const val WATER_ACTIVITY = "$PREFIX/water"

        const val CREATE = WATER_ACTIVITY
        const val GET_BY_ID = "$WATER_ACTIVITY/{activityId}"
        const val UPDATE = "$WATER_ACTIVITY/{activityId}"
        const val REMOVE = "$WATER_ACTIVITY/{activityId}"

        const val CREATE_QUESTIONNAIRE = "$WATER_ACTIVITY/{activityId}/questionnaire"
        const val GET_QUESTIONNAIRE = "$WATER_ACTIVITY/{activityId}/questionnaire"

        const val ADD_WAVE = "$WATER_ACTIVITY/{activityId}/wave"
        const val REMOVE_WAVE = "$WATER_ACTIVITY/{activityId}/wave/{waveId}"

        fun waveById(activityId: Int, waveId: Int): URI =
            UriTemplate(REMOVE_WAVE).expand(activityId, waveId)

        const val ADD_MANEUVER = "$WATER_ACTIVITY/{activityId}/wave/{waveId}/maneuver"
        const val REMOVE_MANEUVER = "$WATER_ACTIVITY/{activityId}/wave/{waveId}/maneuver/{maneuverId}"

        fun maneuverById(activityId: Int, waveId: Int, maneuverId: Int): URI =
            UriTemplate(REMOVE_MANEUVER).expand(activityId, waveId, maneuverId)

        fun byId(id: Int): URI = UriTemplate(GET_BY_ID).expand(id)
    }

    object WaterManeuver {
        private const val WATER_MANEUVERS = "$PREFIX/water/maneuver"

        const val CREATE = WATER_MANEUVERS
        const val GET_BY_ID = "$WATER_MANEUVERS/{maneuverId}"
        const val GET_ALL = WATER_MANEUVERS
        const val UPDATE = "$WATER_MANEUVERS/{maneuverId}"
        const val REMOVE = "$WATER_MANEUVERS/{maneuverId}"

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

        const val CREATE_CALENDAR = "$ATHLETES/{aid}/calendar"
        const val GET_CALENDAR = "$ATHLETES/{aid}/calendar"

        const val GET_WATER_ACTIVITIES = "$ATHLETES/{aid}/water"

        const val CREATE_COMPETITION = "$ATHLETES/{aid}/competition"
        const val GET_COMPETITION_BY_ID = "$ATHLETES/{aid}/competition/{id}"
        const val REMOVE_COMPETITION = "$ATHLETES/{aid}/competition/{id}"

        fun competitionById(aid: Int, id: Int): URI = UriTemplate(GET_COMPETITION_BY_ID).expand(aid, id)

        fun byId(id: Int): URI = UriTemplate(GET_BY_ID).expand(id)

        fun characteristicsByDate(
            aid: Int,
            date: Long,
        ): URI = UriTemplate(GET_CHARACTERISTICS).expand(aid, date)
    }
}

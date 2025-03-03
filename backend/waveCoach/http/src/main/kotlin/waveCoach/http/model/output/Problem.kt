package pt.isel.daw.imSystem.http.model.output

import org.springframework.http.ResponseEntity
import java.net.URI

class Problem(
    val title: String,
    typeUri: URI,
) {
    val type = typeUri.toASCIIString()

    companion object {
        const val MEDIA_TYPE = "application/problem+json"

        fun response(status: Int, problem: Problem) = ResponseEntity
            .status(status)
            .header("Content-Type", MEDIA_TYPE)
            .body<Any>(problem)

        private const val BASE_URI = "https://github.com/isel-leic-daw/2024-daw-leic51d-g14-1/tree/main/docs/problems"

        val adminCannotLeave = Problem(
            "Admin cannot leave channel",
            URI("$BASE_URI/admin-cannot-leave"),
        )

        val appInviteNotFound = Problem(
            "App invite not found",
            URI("$BASE_URI/app-invite-not-found"),
        )

        val channelAlreadyExists = Problem(
            "Channel already exists",
            URI("$BASE_URI/channel-already-exists"),
        )

        val channelInviteNotFound = Problem(
            "Channel invite not found",
            URI("$BASE_URI/channel-invite-not-found"),
        )

        val channelIsPrivate = Problem(
            "Channel is private",
            URI("$BASE_URI/channel-is-private"),
        )

        val channelNotFound = Problem(
            "Channel not found",
            URI("$BASE_URI/channel-not-found"),
        )

        val insecurePassword = Problem(
            "Insecure password",
            URI("$BASE_URI/insecure-password"),
        )

        val invalidChannelId = Problem(
            "Invalid channel id",
            URI("$BASE_URI/invalid-channel-id"),
        )

        val invalidChannelName = Problem(
            "Invalid channel name",
            URI("$BASE_URI/invalid-channel-name"),
        )

        val invalidLogin = Problem(
            "Invalid login",
            URI("$BASE_URI/invalid-login"),
        )

        val invalidSort = Problem(
            "Invalid sort",
            URI("$BASE_URI/invalid-sort"),
        )

        val invalidToken = Problem(
            "Invalid token",
            URI("$BASE_URI/invalid-token"),
        )

        val invalidType = Problem(
            "Invalid type",
            URI("$BASE_URI/invalid-type"),
        )

        val invalidUserId = Problem(
            "Invalid user id",
            URI("$BASE_URI/invalid-user-id"),
        )

        val invitedUserNotFound = Problem(
            "Invited user not found",
            URI("$BASE_URI/invited-user-not-found"),
        )

        val passwordIsBlank = Problem(
            "Password is blank",
            URI("$BASE_URI/password-is-blank"),
        )

        val tokenNotFound = Problem(
            "Token not found",
            URI("$BASE_URI/token-not-found"),
        )

        val userAlreadyExists = Problem(
            "User already exists",
            URI("$BASE_URI/user-already-exists"),
        )

        val userAlreadyInChannel = Problem(
            "User already in channel",
            URI("$BASE_URI/user-already-in-channel"),
        )

        val userCannotGetChannelInvites = Problem(
            "User cannot get channel invites",
            URI("$BASE_URI/user-cannot-get-channel-invites"),
        )

        val userCannotGetChannels = Problem(
            "User cannot get channels",
            URI("$BASE_URI/user-cannot-get-channels"),
        )

        val userCannotInviteToChannel = Problem(
            "User cannot invite to channel",
            URI("$BASE_URI/user-cannot-invite-to-channel"),
        )

        val userCannotJoinChannel = Problem(
            "User cannot join channel",
            URI("$BASE_URI/user-cannot-join-channel"),
        )

        val userCannotLeaveChannel = Problem(
            "User cannot leave channel",
            URI("$BASE_URI/user-cannot-leave-channel"),
        )

        val userNotAdmin = Problem(
            "User not admin",
            URI("$BASE_URI/user-not-admin"),
        )

        val userNotFound = Problem(
            "User not found",
            URI("$BASE_URI/user-not-found"),
        )

        val userNotInChannel = Problem(
            "User not in channel",
            URI("$BASE_URI/user-not-in-channel"),
        )

        val usernameIsBlank = Problem(
            "Username is blank",
            URI("$BASE_URI/username-is-blank"),
        )

        val userCannotGetRole = Problem(
            "User cannot get role",
            URI("$BASE_URI/user-cannot-get-role"),
        )

        val youAreAnObserver = Problem(
            "You are an observer",
            URI("$BASE_URI/you-are-an-observer"),
        )
    }
}
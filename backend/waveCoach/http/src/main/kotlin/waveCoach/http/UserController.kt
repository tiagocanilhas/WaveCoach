package waveCoach.http

import kotlinx.datetime.Clock
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import waveCoach.domain.AuthenticatedUser
import waveCoach.http.model.input.LoginInputModel
import waveCoach.http.model.input.UserUpdatePasswordInputModel
import waveCoach.http.model.input.UserUpdateUsernameInputModel
import waveCoach.http.model.output.AuthCheckOutputModel
import waveCoach.http.model.output.LoginOutputModel
import waveCoach.http.model.output.Problem
import waveCoach.services.CheckCredentialsError
import waveCoach.services.UserServices
import waveCoach.services.UserUpdatePasswordError
import waveCoach.services.UserUpdateUsernameError
import waveCoach.utils.Failure
import waveCoach.utils.Success

@RestController
class UserController(
    private val userServices: UserServices,
) {
    private fun token(
        token: String,
        maxAgeSeconds: Long,
    ) = ResponseCookie.from("token", token)
        .httpOnly(true)
        .sameSite("Strict")
        .secure(true)
        .maxAge(maxAgeSeconds)
        .build()

    @PostMapping(Uris.Users.LOGIN)
    fun login(
        @RequestBody input: LoginInputModel,
    ): ResponseEntity<*> {
        val result = userServices.checkCredentials(input.username, input.password)

        return when (result) {
            is Success ->
                ResponseEntity.status(200)
                    .header(
                        "Set-Cookie",
                        token(
                            result.value.tokenValue,
                            result.value.tokenExpiration.epochSeconds - Clock.System.now().epochSeconds,
                        ).toString(),
                    )
                    .body(LoginOutputModel(result.value.id, result.value.username, result.value.tokenValue, result.value.isCoach))

            is Failure ->
                when (result.value) {
                    CheckCredentialsError.UsernameIsBlank -> Problem.response(400, Problem.usernameIsBlank)
                    CheckCredentialsError.PasswordIsBlank -> Problem.response(400, Problem.passwordIsBlank)
                    CheckCredentialsError.InvalidLogin -> Problem.response(400, Problem.invalidLogin)
                }
        }
    }

    @PostMapping(Uris.Users.LOGOUT)
    fun logout(user: AuthenticatedUser): ResponseEntity<*> {
        userServices.revokeToken(user.token)

        return ResponseEntity.status(200).build<Unit>()
    }

    @GetMapping(Uris.Users.AUTH_CHECK)
    fun authCheck(user: AuthenticatedUser): ResponseEntity<*> {
        return ResponseEntity
            .status(200)
            .body(AuthCheckOutputModel(user.info.id, user.info.username, user.info.isCoach))
    }

    @PatchMapping(Uris.Users.UPDATE_USERNAME)
    fun updateUsername(
        user: AuthenticatedUser,
        @RequestBody input: UserUpdateUsernameInputModel,
    ): ResponseEntity<*> {
        val result = userServices.updateUsername(user.info.id, input.newUsername)

        return when (result) {
            is Success ->
                ResponseEntity.status(204).build<Unit>()

            is Failure ->
                when (result.value) {
                    UserUpdateUsernameError.InvalidUsername -> Problem.response(400, Problem.invalidUsername)
                    UserUpdateUsernameError.UsernameAlreadyExists -> Problem.response(400, Problem.usernameAlreadyExists)
                }
        }
    }

    @PatchMapping(Uris.Users.UPDATE_PASSWORD)
    fun updatePassword(
        user: AuthenticatedUser,
        @RequestBody input: UserUpdatePasswordInputModel
    ): ResponseEntity<*> {
        val result = userServices.updatePassword(user.info.id, input.oldPassword, input.newPassword)

        return when (result) {
            is Success ->
                ResponseEntity.status(204).build<Unit>()

            is Failure ->
                when (result.value) {
                    UserUpdatePasswordError.InvalidOldPassword -> Problem.response(400, Problem.invalidOldPassword)
                    UserUpdatePasswordError.InvalidNewPassword -> Problem.response(400, Problem.insecurePassword)
                    UserUpdatePasswordError.PasswordsAreEqual -> Problem.response(400, Problem.passwordsAreEqual)

                    UserUpdatePasswordError.UserNotFound -> {
                        throw IllegalStateException("User not found when updating password")
                    }
                }
        }
    }

}

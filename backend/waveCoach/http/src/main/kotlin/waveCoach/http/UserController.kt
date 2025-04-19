package waveCoach.http

import kotlinx.datetime.Clock
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import waveCoach.domain.AuthenticatedUser
import waveCoach.http.model.input.AuthCheckOutputModel
import waveCoach.http.model.input.LoginInputModel
import waveCoach.http.model.input.UserUpdateInputModel
import waveCoach.http.model.output.LoginOutputModel
import waveCoach.http.model.output.Problem
import waveCoach.services.CheckCredentialsError
import waveCoach.services.CreateCoachError
import waveCoach.services.UserServices
import waveCoach.services.UserUpdateError
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
        .path("/")
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
                    .body(LoginOutputModel(result.value.id, result.value.username, result.value.tokenValue))

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
    fun authCheck(
        user: AuthenticatedUser
    ): ResponseEntity<*> {
        return ResponseEntity
            .status(200)
            .body(AuthCheckOutputModel(user.info.id, user.info.username,))
    }

    @PutMapping(Uris.Users.UPDATE)
    fun update(
        user: AuthenticatedUser,
        @RequestBody input: UserUpdateInputModel,
    ): ResponseEntity<*> {
        val result = userServices.updateCredentials(user.info.id, input.username, input.password,)

        return when (result) {
            is Success ->
                ResponseEntity.status(204).build<Unit>()

            is Failure ->
                when (result.value) {
                    UserUpdateError.InvalidUsername -> Problem.response(400, Problem.invalidUsername)
                    UserUpdateError.InsecurePassword -> Problem.response(400, Problem.insecurePassword)
                    UserUpdateError.UsernameAlreadyExists -> Problem.response(400, Problem.usernameAlreadyExists)
                }
        }
    }
}

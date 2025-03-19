package waveCoach.http

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import waveCoach.domain.AuthenticatedUser
import waveCoach.http.model.input.LoginInputModel
import waveCoach.http.model.input.CreateUserInputModel
import waveCoach.http.model.output.LoginOutputModel
import waveCoach.http.model.output.Problem
import waveCoach.services.CreateUserError
import waveCoach.services.CheckCredentialsError
import waveCoach.services.UserServices
import waveCoach.utils.Failure
import waveCoach.utils.Success

@RestController
class UserController(
    private val userServices: UserServices,
) {
    @PostMapping(Uris.Users.CREATE)
    fun create(
        @RequestBody input: CreateUserInputModel
    ): ResponseEntity<*> {
        val result = userServices.createUser(input.username, input.password)

        return when (result) {
            is Success -> ResponseEntity
                .status(201)
                .header("Location", Uris.Users.byId(result.value).toASCIIString())
                .build<Unit>()

            is Failure -> when (result.value) {
                CreateUserError.InvalidUsername -> Problem.response(400, Problem.invalidUsername)
                CreateUserError.InsecurePassword -> Problem.response(400, Problem.insecurePassword)
                CreateUserError.UsernameAlreadyExists -> Problem.response(400, Problem.usernameAlreadyExists)
            }
        }
    }

    @PostMapping(Uris.Users.LOGIN)
    fun login(
        @RequestBody input: LoginInputModel
    ): ResponseEntity<*> {
        val result = userServices.checkCredentials(input.username, input.password)

        return when (result) {
            is Success -> ResponseEntity.status(200)
                .body(LoginOutputModel(result.value.id, result.value.username, result.value.tokenValue))

            is Failure -> when (result.value) {
                CheckCredentialsError.UsernameIsBlank -> Problem.response(400, Problem.usernameIsBlank)
                CheckCredentialsError.PasswordIsBlank -> Problem.response(400, Problem.passwordIsBlank)
                CheckCredentialsError.InvalidLogin -> Problem.response(400, Problem.invalidLogin)
            }
        }
    }

    @PostMapping(Uris.Users.LOGOUT)
    fun logout(
        user: AuthenticatedUser,
    ): ResponseEntity<*> {
        userServices.revokeToken(user.token)

        return ResponseEntity.status(200).build<Unit>()
    }
}
package waveCoach.http

import kotlinx.datetime.Clock
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import waveCoach.http.model.input.LoginInputModel
import waveCoach.http.model.input.CreateUserInputModel
import waveCoach.http.model.output.LoginOutputModel
import waveCoach.http.model.output.Problem
import waveCoach.services.CreateUserError
import waveCoach.services.LoginError
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
                CreateUserError.InsecurePassword -> Problem.response(400, Problem.insecurePassword)
                CreateUserError.UsernameAlreadyExists -> Problem.response(400, Problem.usernameAlreadyExists)
            }
        }
    }

    @PostMapping(Uris.Users.LOGIN)
    fun login(
        @RequestBody input: LoginInputModel
    ): ResponseEntity<*> {
        val result = userServices.login(input.username, input.password)

        return when (result) {
            is Success -> ResponseEntity.status(200)
                .body(LoginOutputModel(result.value.id, result.value.username, result.value.tokenValue))

            is Failure -> when (result.value) {
                LoginError.UsernameIsBlank -> Problem.response(400, Problem.usernameIsBlank)
                LoginError.PasswordIsBlank -> Problem.response(400, Problem.passwordIsBlank)
                LoginError.InvalidLogin -> Problem.response(400, Problem.invalidLogin)
            }
        }
    }

    @PostMapping(Uris.Users.LOGOUT)
    fun logout(): ResponseEntity<*> {
        TODO("Not yet implemented")
    }


}
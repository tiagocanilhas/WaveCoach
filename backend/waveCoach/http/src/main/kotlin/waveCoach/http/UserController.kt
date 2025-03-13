package waveCoach.http

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import waveCoach.http.model.input.LoginInputModel
import waveCoach.http.model.input.CreateUserInputModel
import waveCoach.http.model.output.Problem
import waveCoach.services.CreateUserError
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
        TODO("Not yet implemented")
    }

    @PostMapping(Uris.Users.LOGOUT)
    fun logout(): ResponseEntity<*> {
        TODO("Not yet implemented")
    }


}
package waveCoach.http

import org.springframework.web.bind.annotation.RestController
import waveCoach.services.UserServices

@RestController
class UserController(
    private val userServices: UserServices,
) {
    // TODO: Implement UserController
}
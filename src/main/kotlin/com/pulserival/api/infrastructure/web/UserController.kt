package com.pulserival.api.infrastructure.web

import com.pulserival.api.application.service.RegisterUserCommand
import com.pulserival.api.application.service.UserRegistrationService
import com.pulserival.api.application.service.UserResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val registrationService: UserRegistrationService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun registerUser(@RequestBody command: RegisterUserCommand): UserResponse {
        return registrationService.register(command)
    }

    @GetMapping("/{id}")
    fun getUser(@PathVariable id: UUID): UserResponse {
        return registrationService.getUser(id)
    }
}

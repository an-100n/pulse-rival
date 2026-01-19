package com.pulserival.identity.controller

import com.pulserival.identity.dto.RegisterUserCommand
import com.pulserival.identity.dto.UserResponse
import com.pulserival.identity.service.UserRegistrationService
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

package com.pulserival.identity.controller

import com.pulserival.identity.dto.RegisterUserCommand
import com.pulserival.identity.dto.UpdateUserProfileCommand
import com.pulserival.identity.dto.UserResponse
import com.pulserival.identity.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun registerUser(@RequestBody command: RegisterUserCommand): UserResponse {
        return userService.register(command)
    }

    @GetMapping("/{id}")
    fun getUser(@PathVariable id: UUID): UserResponse {
        return userService.getUser(id)
    }

    @PutMapping("/{id}")
    fun updateProfile(@PathVariable id: UUID, @RequestBody command: UpdateUserProfileCommand): UserResponse {
        return userService.updateProfile(id, command)
    }
}

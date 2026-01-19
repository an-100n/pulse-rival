package com.pulserival.api.application.service

import com.pulserival.api.domain.exception.EmailAlreadyInUseException
import com.pulserival.api.domain.exception.UsernameAlreadyTakenException
import com.pulserival.api.domain.exception.UserNotFoundException
import com.pulserival.api.domain.model.User
import com.pulserival.api.infrastructure.persistence.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

// DTOs (Data Transfer Objects)
data class RegisterUserCommand(
    val username: String,
    val email: String,
    val timezone: String?
)

data class UserResponse(
    val id: String,
    val username: String,
    val email: String,
    val timezone: String
)

@Service
class UserRegistrationService(
    private val userRepository: UserRepository
) {

    @Transactional
    fun register(command: RegisterUserCommand): UserResponse {
        if (userRepository.existsByEmail(command.email)) {
            throw EmailAlreadyInUseException(command.email)
        }
        if (userRepository.existsByUsername(command.username)) {
            throw UsernameAlreadyTakenException(command.username)
        }

        val newUser = User(
            username = command.username,
            email = command.email,
            timezone = command.timezone ?: "UTC"
        )

        val savedUser = userRepository.save(newUser)

        return UserResponse(
            id = savedUser.id.toString(),
            username = savedUser.username,
            email = savedUser.email,
            timezone = savedUser.timezone
        )
    }

    @Transactional(readOnly = true)
    fun getUser(id: UUID): UserResponse {
        val user = userRepository.findById(id)
            .orElseThrow { UserNotFoundException(id.toString()) }
        
        return UserResponse(
            id = user.id.toString(),
            username = user.username,
            email = user.email,
            timezone = user.timezone
        )
    }
}

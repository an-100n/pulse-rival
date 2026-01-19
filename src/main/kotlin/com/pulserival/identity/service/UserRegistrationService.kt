package com.pulserival.identity.service

import com.pulserival.common.exception.EmailAlreadyInUseException
import com.pulserival.common.exception.UsernameAlreadyTakenException
import com.pulserival.common.exception.UserNotFoundException
import com.pulserival.identity.dto.RegisterUserCommand
import com.pulserival.identity.dto.UserResponse
import com.pulserival.identity.entity.User
import com.pulserival.identity.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID


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

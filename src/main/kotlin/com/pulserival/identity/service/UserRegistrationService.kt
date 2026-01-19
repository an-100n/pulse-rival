package com.pulserival.identity.service

import com.pulserival.common.exception.EmailAlreadyInUseException
import com.pulserival.common.exception.UsernameAlreadyTakenException
import com.pulserival.common.exception.UserNotFoundException
import com.pulserival.identity.dto.RegisterUserCommand
import com.pulserival.identity.dto.UserResponse
import com.pulserival.identity.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID


@Service
class UserRegistrationService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    @Transactional
    fun register(command: RegisterUserCommand): UserResponse {
        if (userRepository.existsByEmail(command.email)) {
            throw EmailAlreadyInUseException(command.email)
        }
        if (userRepository.existsByDbUsername(command.username)) {
            throw UsernameAlreadyTakenException(command.username)
        }

        val rawPassword = command.password
        val encodedPassword: String = passwordEncoder.encode(rawPassword)!!

        val newUser = com.pulserival.identity.entity.User(
            dbUsername = command.username,
            email = command.email,
            dbPassword = encodedPassword,
            timezone = command.timezone ?: "UTC"
        )

        val savedUser = userRepository.save(newUser)

        return UserResponse(
            id = savedUser.id.toString(),
            username = savedUser.dbUsername,
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
            username = user.dbUsername,
            email = user.email,
            timezone = user.timezone
        )
    }
}

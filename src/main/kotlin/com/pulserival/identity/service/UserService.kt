package com.pulserival.identity.service

import com.pulserival.common.exception.EmailAlreadyInUseException
import com.pulserival.common.exception.UsernameAlreadyTakenException
import com.pulserival.common.exception.UserNotFoundException
import com.pulserival.identity.dto.RegisterUserCommand
import com.pulserival.identity.dto.UpdateUserProfileCommand
import com.pulserival.identity.dto.UserResponse
import com.pulserival.identity.entity.User
import com.pulserival.identity.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class UserService(
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

        val newUser = User(
            dbUsername = command.username,
            email = command.email,
            dbPassword = encodedPassword,
            timezone = command.timezone ?: "UTC"
        )

        val savedUser = userRepository.save(newUser)

        return mapToResponse(savedUser)
    }

    @Transactional(readOnly = true)
    fun getUser(id: UUID): UserResponse {
        val user = userRepository.findById(id)
            .orElseThrow { UserNotFoundException(id.toString()) }
        
        return mapToResponse(user)
    }

    @Transactional
    fun updateProfile(userId: UUID, command: UpdateUserProfileCommand): UserResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { UserNotFoundException(userId.toString()) }

        command.sex?.let { user.sex = it }
        command.heightCm?.let { user.heightCm = it }
        command.weightKg?.let { user.weightKg = it }
        command.birthDate?.let { user.birthDate = it }
        command.timezone?.let { user.timezone = it }

        val savedUser = userRepository.save(user)
        return mapToResponse(savedUser)
    }

    private fun mapToResponse(user: User): UserResponse {
        return UserResponse(
            id = user.id.toString(),
            username = user.dbUsername,
            email = user.email,
            timezone = user.timezone,
            sex = user.sex,
            heightCm = user.heightCm,
            weightKg = user.weightKg,
            birthDate = user.birthDate,
            age = user.getAge()
        )
    }
}

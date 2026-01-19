package com.pulserival.identity.service

import com.pulserival.identity.dto.AuthRequest
import com.pulserival.identity.dto.AuthResponse
import com.pulserival.identity.repository.UserRepository
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class AuthenticationService(
    private val authenticationManager: AuthenticationManager,
    private val jwtService: JwtService,
    private val userRepository: UserRepository
) {

    fun authenticate(request: AuthRequest): AuthResponse {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                request.username,
                request.password
            )
        )
        // If we get here, the user is authenticated (password matched)
        
        val user = userRepository.findByDbUsername(request.username)
            ?: throw UsernameNotFoundException("User not found") // Should not happen if auth passed
            
        val token = jwtService.generateToken(user)
        
        return AuthResponse(token)
    }
}

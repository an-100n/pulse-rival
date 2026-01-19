package com.pulserival.identity.dto

data class RegisterUserCommand(
    val username: String,
    val email: String,
    val password: String,
    val timezone: String?
)

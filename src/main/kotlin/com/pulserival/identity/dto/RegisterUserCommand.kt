package com.pulserival.identity.dto

data class RegisterUserCommand(
    val username: String,
    val email: String,
    val timezone: String?
)
package com.pulserival.identity.dto

data class UserResponse(
    val id: String,
    val username: String,
    val email: String,
    val timezone: String
)

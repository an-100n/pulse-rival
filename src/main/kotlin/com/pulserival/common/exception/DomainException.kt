package com.pulserival.common.exception

sealed class DomainException(message: String) : RuntimeException(message)

class EmailAlreadyInUseException(email: String) : DomainException("Email '$email' is already in use")
class UsernameAlreadyTakenException(username: String) : DomainException("Username '$username' is already taken")
class InvalidActivityValueException(message: String) : DomainException(message)
class UserNotFoundException(userId: String) : DomainException("User with ID '$userId' not found")

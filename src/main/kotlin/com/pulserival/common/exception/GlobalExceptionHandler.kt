package com.pulserival.common.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.net.URI
import java.time.Instant

@RestControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(EmailAlreadyInUseException::class, UsernameAlreadyTakenException::class)
    fun handleConflict(ex: DomainException): ProblemDetail {
        return createProblemDetail(ex, HttpStatus.CONFLICT, "resource-conflict")
    }

    @ExceptionHandler(InvalidActivityValueException::class)
    fun handleBadRequest(ex: DomainException): ProblemDetail {
        return createProblemDetail(ex, HttpStatus.BAD_REQUEST, "invalid-request")
    }

    @ExceptionHandler(UserNotFoundException::class)
    fun handleNotFound(ex: DomainException): ProblemDetail {
        return createProblemDetail(ex, HttpStatus.NOT_FOUND, "resource-not-found")
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(ex: IllegalArgumentException): ProblemDetail {
        return createProblemDetail(ex, HttpStatus.BAD_REQUEST, "invalid-argument")
    }

    private fun createProblemDetail(
        ex: Exception,
        status: HttpStatus,
        type: String
    ): ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(status, ex.message ?: "An unexpected error occurred")
        problemDetail.title = status.reasonPhrase
        problemDetail.type = URI.create("https://pulserival.com/errors/$type")
        problemDetail.setProperty("timestamp", Instant.now())
        return problemDetail
    }
}

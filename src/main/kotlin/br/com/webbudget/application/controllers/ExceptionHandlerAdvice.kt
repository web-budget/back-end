package br.com.webbudget.application.controllers

import br.com.webbudget.domain.exceptions.BusinessException
import br.com.webbudget.domain.exceptions.ConflictingPropertyException
import br.com.webbudget.domain.exceptions.ErrorCodes.FIELD_VALIDATION_FAILED
import br.com.webbudget.domain.exceptions.ResourceNotFoundException
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletResponse
import org.springframework.dao.NonTransientDataAccessException
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY
import org.springframework.http.ProblemDetail
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

private val logger = KotlinLogging.logger {}

@RestControllerAdvice
class ExceptionHandlerAdvice {

    @ExceptionHandler(BadCredentialsException::class)
    fun handle(response: HttpServletResponse, ex: BadCredentialsException) {
        response.sendError(HttpStatus.UNAUTHORIZED.value(), ex.message)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handle(ex: IllegalArgumentException): ProblemDetail {
        logger.error(ex) { "Illegal argument exception" }
        return ProblemDetail.forStatusAndDetail(BAD_REQUEST, ex.message ?: NO_MESSAGE_PROVIDED)
    }

    @ExceptionHandler(NonTransientDataAccessException::class)
    fun handle(ex: NonTransientDataAccessException): ProblemDetail {
        logger.error(ex) { "Non transient data access exception" }
        return ProblemDetail.forStatusAndDetail(BAD_REQUEST, ex.message ?: NO_MESSAGE_PROVIDED)
    }

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handle(ex: ResourceNotFoundException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(NOT_FOUND, ex.message ?: NO_MESSAGE_PROVIDED)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handle(ex: MethodArgumentNotValidException): ProblemDetail {

        val violations = mutableMapOf<String, String?>()
        for (fieldError in ex.bindingResult.fieldErrors) {
            violations[fieldError.field] = fieldError.defaultMessage
        }

        val message = Message(FIELD_VALIDATION_FAILED, violations)

        return buildDetails(UNPROCESSABLE_ENTITY, "Some fields are missing or invalid", message)
    }

    @ExceptionHandler(BusinessException::class)
    fun handle(ex: BusinessException): ProblemDetail =
        buildDetails(BAD_REQUEST, ex.message ?: NO_MESSAGE_PROVIDED, Message(ex.key, ex.parameters))

    @ExceptionHandler(ConflictingPropertyException::class)
    fun handle(ex: ConflictingPropertyException): ProblemDetail =
        buildDetails(CONFLICT, ex.message ?: NO_MESSAGE_PROVIDED, Message(ex.key, ex.parameters))

    private fun buildDetails(status: HttpStatus, detail: String, message: Message): ProblemDetail {

        val detail = ProblemDetail.forStatusAndDetail(status, detail)
        detail.setProperty("message", message)

        return detail
    }

    data class Message(val key: String, val parameters: Map<String, Any?>? = null)

    companion object {
        private const val NO_MESSAGE_PROVIDED = "No detail message provided"
    }
}

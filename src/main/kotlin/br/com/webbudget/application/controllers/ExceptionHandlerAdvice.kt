package br.com.webbudget.application.controllers

import br.com.webbudget.domain.exceptions.ApplicationException
import br.com.webbudget.domain.exceptions.DomainException
import br.com.webbudget.domain.exceptions.ErrorCodes.FIELD_VALIDATION_FAILED
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.NonTransientDataAccessException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY
import org.springframework.http.HttpStatusCode
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

private val log = KotlinLogging.logger {}

/**
 * Central translation of exceptions into responses. Every body follows Spring's [ProblemDetail]
 * shape with two extra, machine-readable properties so the front-end has a single way to react:
 *
 * - `code`: a stable identifier of the problem (e.g. `resource-not-found`, `field-validation-failed`).
 * - `parameters`: optional contextual values for the message (e.g. the conflicting field/value).
 *
 * Unexpected errors are logged in full server-side and answered with a generic detail so no
 * sensitive information ever reaches the client. Standard Spring MVC exceptions keep their default
 * handling via [ResponseEntityExceptionHandler] and are therefore not swallowed by the catch-all.
 */
@RestControllerAdvice
class ExceptionHandlerAdvice : ResponseEntityExceptionHandler() {

    @ExceptionHandler(DomainException::class)
    fun handle(ex: DomainException): ProblemDetail =
        buildProblem(ex.status, ex.code, ex.message ?: NO_MESSAGE_PROVIDED, ex.parameters)

    @ExceptionHandler(ApplicationException::class)
    fun handle(ex: ApplicationException): ProblemDetail =
        buildProblem(ex.status, ex.code, ex.message ?: NO_MESSAGE_PROVIDED)

    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentials(): ProblemDetail =
        buildProblem(UNAUTHORIZED, BAD_CREDENTIALS_CODE, "The provided credentials are invalid")

    @ExceptionHandler(IllegalArgumentException::class)
    fun handle(ex: IllegalArgumentException): ProblemDetail {
        log.error(ex) { "Illegal argument exception" }
        return buildProblem(BAD_REQUEST, BAD_REQUEST_CODE, GENERIC_BAD_REQUEST_DETAIL)
    }

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handle(ex: DataIntegrityViolationException): ProblemDetail {
        log.error(ex) { "Data integrity violation" }
        return buildProblem(BAD_REQUEST, DATA_INTEGRITY_VIOLATION_CODE, GENERIC_DATA_INTEGRITY_DETAIL)
    }

    @ExceptionHandler(NonTransientDataAccessException::class)
    fun handle(ex: NonTransientDataAccessException): ProblemDetail {
        log.error(ex) { "Non transient data access exception" }
        return buildProblem(BAD_REQUEST, BAD_REQUEST_CODE, GENERIC_BAD_REQUEST_DETAIL)
    }

    @ExceptionHandler(Exception::class)
    fun handle(ex: Exception): ProblemDetail {
        log.error(ex) { "Unexpected exception caught by the global handler" }
        return buildProblem(INTERNAL_SERVER_ERROR, INTERNAL_ERROR_CODE, GENERIC_INTERNAL_ERROR_DETAIL)
    }

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any> {
        val violations = ex.bindingResult.fieldErrors.associate { it.field to it.defaultMessage }
        val problem = buildProblem(
            UNPROCESSABLE_ENTITY,
            FIELD_VALIDATION_FAILED,
            "Some fields are missing or invalid",
            violations
        )
        return ResponseEntity.status(UNPROCESSABLE_ENTITY).body(problem)
    }

    private fun buildProblem(
        status: HttpStatus,
        code: String,
        detail: String,
        parameters: Map<String, Any?>? = null
    ): ProblemDetail {
        val problem = ProblemDetail.forStatusAndDetail(status, detail)
        problem.setProperty("code", code)
        parameters?.let { problem.setProperty("parameters", it) }
        return problem
    }

    companion object {
        private const val NO_MESSAGE_PROVIDED = "No detail message provided"
        private const val BAD_CREDENTIALS_CODE = "bad-credentials"
        private const val BAD_REQUEST_CODE = "bad-request"
        private const val DATA_INTEGRITY_VIOLATION_CODE = "data-integrity-violation"
        private const val INTERNAL_ERROR_CODE = "internal-error"
        private const val GENERIC_BAD_REQUEST_DETAIL = "The request could not be processed"
        private const val GENERIC_DATA_INTEGRITY_DETAIL =
            "The operation could not be completed because this record is linked to other records"
        private const val GENERIC_INTERNAL_ERROR_DETAIL = "An unexpected error occurred, please try again later"
    }
}

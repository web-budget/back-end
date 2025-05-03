package br.com.webbudget.application.controllers.advice

import br.com.webbudget.domain.exceptions.BusinessException
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletResponse
import org.springframework.dao.NonTransientDataAccessException
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.ProblemDetail
import org.springframework.security.authentication.BadCredentialsException
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
        return ProblemDetail.forStatusAndDetail(BAD_REQUEST, ex.message ?: NO_DETAIL_PROVIDED)
    }

    @ExceptionHandler(NonTransientDataAccessException::class)
    fun handle(ex: NonTransientDataAccessException): ProblemDetail {
        logger.error(ex) { "Non transient data access exception" }
        return ProblemDetail.forStatusAndDetail(BAD_REQUEST, ex.message ?: NO_DETAIL_PROVIDED)
    }

    @ExceptionHandler(BusinessException::class)
    fun handle(ex: BusinessException): ProblemDetail {
        logger.error(ex) { "Business exception" }
        return ProblemDetail.forStatusAndDetail(ex.httpStatus, ex.message ?: NO_DETAIL_PROVIDED)
    }

    companion object {
        private const val NO_DETAIL_PROVIDED = "No detail message provided"
    }
}

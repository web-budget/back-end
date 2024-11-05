package br.com.webbudget.application.controllers.advice

import br.com.webbudget.domain.exceptions.BusinessException
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.ProblemDetail
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ExceptionHandlerAdvice {

    @ExceptionHandler(BadCredentialsException::class)
    fun handle(response: HttpServletResponse, ex: BadCredentialsException) {
        response.sendError(HttpStatus.UNAUTHORIZED.value(), ex.message)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handle(ex: IllegalArgumentException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(BAD_REQUEST, ex.message ?: NO_DETAIL_PROVIDED)

    @ExceptionHandler(BusinessException::class)
    fun handle(ex: BusinessException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(ex.httpStatus, ex.message ?: NO_DETAIL_PROVIDED)

    companion object {
        private const val NO_DETAIL_PROVIDED = "No detail message provided"
    }
}

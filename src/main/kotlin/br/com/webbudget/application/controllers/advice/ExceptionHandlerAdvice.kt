package br.com.webbudget.application.controllers.advice

import br.com.webbudget.domain.exceptions.BusinessException
import jakarta.servlet.http.HttpServletResponse
import org.springframework.dao.NonTransientDataAccessException
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.ProblemDetail
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionHandlerAdvice {

    @ExceptionHandler(BadCredentialsException::class)
    fun handle(response: HttpServletResponse, ex: BadCredentialsException) {
        response.sendError(HttpStatus.UNAUTHORIZED.value(), ex.message)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handle(ex: IllegalArgumentException): ProblemDetail {

        val error = MappedErrors.errors.getOrDefault(ex::class, NO_ERROR_PROVIDED)
        val detail = ex.message ?: NO_DETAIL_PROVIDED

        return asProblemDetail(error, detail)
    }

    @ExceptionHandler(NonTransientDataAccessException::class)
    fun handle(ex: NonTransientDataAccessException): ProblemDetail {

        val error = MappedErrors.errors.getOrDefault(ex::class, NO_ERROR_PROVIDED)
        val detail = ex.message ?: NO_DETAIL_PROVIDED

        return asProblemDetail(error, detail)
    }

    @ExceptionHandler(BusinessException::class)
    fun handle(ex: BusinessException): ProblemDetail {
        return asProblemDetail(ex.message ?: NO_ERROR_PROVIDED, ex.detail, ex.httpStatus)
    }

    private fun asProblemDetail(error: String, detail: String, status: HttpStatus = BAD_REQUEST): ProblemDetail {

        val problemDetail = ProblemDetail.forStatusAndDetail(status, detail)
        problemDetail.setProperty("error", error)

        return problemDetail
    }

    companion object {
        private const val NO_ERROR_PROVIDED = "No error message provided"
        private const val NO_DETAIL_PROVIDED = "No detail message provided"
    }
}

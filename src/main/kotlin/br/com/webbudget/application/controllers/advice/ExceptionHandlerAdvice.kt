package br.com.webbudget.application.controllers.advice

import br.com.webbudget.domain.exceptions.BadRefreshTokenException
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import javax.servlet.http.HttpServletResponse

@ControllerAdvice
class ExceptionHandlerAdvice {

    @ExceptionHandler(BadCredentialsException::class)
    fun handle(response: HttpServletResponse, ex: BadCredentialsException) {
        response.sendError(HttpStatus.UNAUTHORIZED.value(), ex.message)
    }

    @ExceptionHandler(BadRefreshTokenException::class)
    fun handle(response: HttpServletResponse, ex: BadRefreshTokenException) {
        response.sendError(HttpStatus.UNAUTHORIZED.value(), ex.message)
    }
}

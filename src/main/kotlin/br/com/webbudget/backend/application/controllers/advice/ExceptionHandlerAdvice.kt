package br.com.webbudget.backend.application.controllers.advice

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
}
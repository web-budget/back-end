package br.com.webbudget.application.controllers.advice

import br.com.webbudget.application.payloads.ValidationError
import br.com.webbudget.application.payloads.Violation
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice
class ValidationAdvice {

    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handle(ex: MethodArgumentNotValidException): ValidationError {

        val violations = mutableListOf<Violation>()

        for (error in ex.bindingResult.fieldErrors) {
            violations.add(Violation(error.field, error.defaultMessage))
        }

        return ValidationError(violations)
    }
}
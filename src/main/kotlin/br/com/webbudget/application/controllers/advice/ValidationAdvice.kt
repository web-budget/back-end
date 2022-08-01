package br.com.webbudget.application.controllers.advice

import br.com.webbudget.application.payloads.validation.ValidationError
import br.com.webbudget.application.payloads.validation.Violation
import br.com.webbudget.domain.exceptions.BusinessException
import br.com.webbudget.domain.exceptions.DuplicatedPropertyException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import javax.validation.ConstraintViolationException

@ControllerAdvice
class ValidationAdvice {

    @ResponseBody
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DuplicatedPropertyException::class)
    fun handle(ex: BusinessException): Violation {
        return Violation(ex.message!!, ex.detail) // FIXME remove the assert to not null
    }

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

    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(ConstraintViolationException::class)
    fun handle(ex: ConstraintViolationException): ValidationError {

        val violations = mutableListOf<Violation>()

        for (constraintViolation in ex.constraintViolations) {
            violations.add(Violation(constraintViolation.propertyPath.toString(), constraintViolation.message))
        }

        return ValidationError(violations)
    }
}

package br.com.webbudget.application.controllers.advice

import br.com.webbudget.domain.exceptions.DuplicatedPropertyException
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ValidationHandlerAdvice {

    @ExceptionHandler(DuplicatedPropertyException::class)
    fun handle(ex: DuplicatedPropertyException): ProblemDetail {

        val detail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, CONFLICT_ERROR)

        detail.setProperty("error", ex.message!!)
        detail.setProperty("property", ex.property)

        return detail
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handle(ex: MethodArgumentNotValidException): ProblemDetail {

        val errors = mutableMapOf<String, String?>()
        for (error in ex.bindingResult.fieldErrors) {
            errors[error.field] = error.defaultMessage
        }

        val detail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, INVALID_OR_MISSING_FIELDS)

        detail.title = UNPROCESSABLE_PAYLOAD
        detail.setProperty(ERRORS_PROPERTY, errors)

        return detail
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handle(ex: ConstraintViolationException): ProblemDetail {

        val errors = mutableMapOf<String, String?>()
        for (violation in ex.constraintViolations) {
            errors[violation.propertyPath.toString()] = violation.message
        }

        val detail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, INVALID_OR_MISSING_FIELDS)

        detail.title = UNPROCESSABLE_PAYLOAD
        detail.setProperty(ERRORS_PROPERTY, errors)

        return detail
    }

    companion object {
        private const val ERRORS_PROPERTY = "errors"
        private const val UNPROCESSABLE_PAYLOAD = "Unprocessable payload"
        private const val INVALID_OR_MISSING_FIELDS = "Some fields are missing or invalid"
        private const val CONFLICT_ERROR = "Other resource is using the same property value"
    }
}

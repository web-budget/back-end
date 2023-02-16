package br.com.webbudget.application.controllers.advice

import br.com.webbudget.domain.exceptions.DuplicatedPropertyException
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ValidationAdvice {

    @ExceptionHandler(DuplicatedPropertyException::class)
    fun handle(ex: DuplicatedPropertyException): ProblemDetail {

        val problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT, "Other resource is using the same property value"
        )

        problemDetail.setProperty("error", ex.message!!)
        problemDetail.setProperty("property", ex.property)

        return problemDetail
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handle(ex: MethodArgumentNotValidException): ProblemDetail {

        val errors = mutableMapOf<String, String?>()
        for (error in ex.bindingResult.fieldErrors) {
            errors[error.field] = error.defaultMessage
        }

        val problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.UNPROCESSABLE_ENTITY,
            "Some fields are missing or invalid"
        )

        problemDetail.title = "Unprocessable payload"
        problemDetail.setProperty(ERRORS_PROPERTY, errors)

        return problemDetail
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handle(ex: ConstraintViolationException): ProblemDetail {

        val errors = mutableMapOf<String, String?>()
        for (violation in ex.constraintViolations) {
            errors[violation.propertyPath.toString()] = violation.message
        }

        val problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.UNPROCESSABLE_ENTITY,
            "Some fields are missing or invalid"
        )

        problemDetail.title = "Unprocessable paylod"
        problemDetail.setProperty(ERRORS_PROPERTY, errors)

        return problemDetail
    }

    companion object {
        private const val ERRORS_PROPERTY = "errors"
    }
}

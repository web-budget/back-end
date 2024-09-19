package br.com.webbudget.application.controllers.advice

import br.com.webbudget.domain.exceptions.ConflictingPropertyException
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ValidationHandlerAdvice {

    @ExceptionHandler(ConflictingPropertyException::class)
    fun handle(ex: ConflictingPropertyException): ProblemDetail {

        val detail = ProblemDetail.forStatusAndDetail(CONFLICT, ex.message)
        detail.setProperty("conflicts", ex.conflicts)

        return detail
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handle(ex: MethodArgumentNotValidException): ProblemDetail {

        val violations = mutableMapOf<String, String?>()
        for (fieldError in ex.bindingResult.fieldErrors) {
            violations[fieldError.field] = fieldError.defaultMessage
        }

        val detail = ProblemDetail.forStatusAndDetail(UNPROCESSABLE_ENTITY, "Some fields are missing or invalid")
        detail.setProperty("violations", violations)

        return detail
    }
}

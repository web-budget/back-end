package br.com.webbudget.controllers.advice

import br.com.webbudget.domain.exceptions.DuplicatedPropertyException
import org.springframework.core.MethodParameter
import org.springframework.http.ResponseEntity
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/validation-exceptions")
class ValidationExceptionController {

    @GetMapping("/duplicated-property-exception")
    fun getBadCredentialsException(): ResponseEntity<Any> {
        throw DuplicatedPropertyException("The message", "The property")
    }

    @GetMapping("/method-argument-no-valid-exception")
    fun getMethodArgumentNotValidException(): ResponseEntity<Any> {

        val method = this.javaClass.getMethod("theMethod", String::class.java)
        val methodParameter = MethodParameter(method, 0)

        val bindingResult = BeanPropertyBindingResult("The target", "The object")

        throw MethodArgumentNotValidException(methodParameter, bindingResult)
    }

    fun theMethod(theParameter: String) {
        println("Dummy method")
    }
}
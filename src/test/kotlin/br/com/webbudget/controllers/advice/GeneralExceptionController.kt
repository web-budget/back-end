package br.com.webbudget.controllers.advice

import br.com.webbudget.domain.exceptions.BusinessException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/general-exceptions")
class GeneralExceptionController {

    @GetMapping("/bad-credentials-exception")
    fun getBadCredentialsException(): ResponseEntity<Any> {
        throw BadCredentialsException("Bad credentials")
    }

    @GetMapping("/illegal-argument-exception")
    fun getIllegalArgumentException(): ResponseEntity<Any> {
        throw IllegalArgumentException("Illegal argument")
    }

    @GetMapping("/business-exception")
    fun getBusinessException(): ResponseEntity<Any> {
        throw BusinessException("The message", "The detail")
    }

    @GetMapping("/non-transient-data-access-exception")
    fun getNonTransientDataAccessException(): ResponseEntity<Any> {
        throw DataIntegrityViolationException("Data integrity violation")
    }
}
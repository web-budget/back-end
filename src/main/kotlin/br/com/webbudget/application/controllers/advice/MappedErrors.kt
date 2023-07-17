package br.com.webbudget.application.controllers.advice

import br.com.webbudget.domain.exceptions.BusinessException
import br.com.webbudget.domain.exceptions.ResourceNotFoundException
import org.springframework.dao.DataIntegrityViolationException
import kotlin.reflect.KClass

object MappedErrors {

    val errors: Map<KClass<out RuntimeException>, String> = mapOf(
        DataIntegrityViolationException::class to "errors.data-integrity-violation",
        BusinessException::class to "errors.business-error",
        IllegalArgumentException::class to "errors.unknown-error",
        ResourceNotFoundException::class to "errors.resource-not-found",
    )
}

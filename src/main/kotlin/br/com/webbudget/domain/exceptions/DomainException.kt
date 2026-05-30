package br.com.webbudget.domain.exceptions

import org.springframework.http.HttpStatus

/**
 * Represents a problem in the domain layer: a business rule violation or an invalid/recoverable state reached while
 * processing a request. Carries a machine-readable [code] and optional [parameters] so the client can render a
 * localized, contextual message.
 *
 * Use the [conflict] factory when the violation is caused by data conflicting with existing records.
 */
open class DomainException(
    message: String,
    val code: String,
    val parameters: Map<String, Any?>? = null,
    val status: HttpStatus = HttpStatus.BAD_REQUEST
) : RuntimeException(message) {

    companion object {

        private const val CONFLICTING_PROPERTIES = "conflicting-properties"
        private const val DEFAULT_CONFLICT_MESSAGE = "Some properties are conflicting with existent data"

        fun conflict(
            parameters: Map<String, Any?>? = null,
            message: String = DEFAULT_CONFLICT_MESSAGE,
            code: String = CONFLICTING_PROPERTIES
        ): DomainException = DomainException(message, code, parameters, HttpStatus.CONFLICT)
    }
}

package br.com.webbudget.domain.exceptions

import org.springframework.http.HttpStatus

/**
 * Represents a problem in the application layer: a request that cannot be fulfilled as asked, such as a missing
 * resource. Carries a machine-readable [code] and the HTTP [status] to expose.
 */
open class ApplicationException(
    val code: String,
    val status: HttpStatus,
    message: String = code
) : RuntimeException(message)

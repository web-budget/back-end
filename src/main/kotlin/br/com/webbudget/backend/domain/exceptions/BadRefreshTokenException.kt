package br.com.webbudget.backend.domain.exceptions

class BadRefreshTokenException : RuntimeException {

    constructor(message: String) : super(message)

    constructor(message: String, cause: Throwable) : super(message, cause)
}

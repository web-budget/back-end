package br.com.webbudget.domain.exceptions

class BusinessException : RuntimeException {

    val detail: String

    constructor(message: String, detail: String) : super(message) {
        this.detail = detail
    }

    constructor(message: String, detail: String, cause: Throwable) : super(message, cause) {
        this.detail = detail
    }
}

package br.com.webbudget.domain.exceptions

open class DuplicatedPropertyException(message: String, val property: String) : RuntimeException(message)

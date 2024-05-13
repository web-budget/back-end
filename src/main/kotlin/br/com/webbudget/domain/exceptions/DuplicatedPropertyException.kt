package br.com.webbudget.domain.exceptions

// TODO rename this class to something more usable
open class DuplicatedPropertyException(message: String, val property: String) : RuntimeException(message)

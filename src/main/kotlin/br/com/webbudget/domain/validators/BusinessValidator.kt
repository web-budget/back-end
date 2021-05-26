package br.com.webbudget.domain.validators

interface BusinessValidator<T> {

    fun validate(value: T)
}

package br.com.webbudget.domain.validators

fun interface BusinessValidator<T> {

    fun validate(value: T)
}

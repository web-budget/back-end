package br.com.webbudget.domain.services

interface ValidationService<T> {

    fun validateOnCreate(value: T)

    fun validateOnUpdate(value: T)

    fun validateOnDelete(value: T)
}
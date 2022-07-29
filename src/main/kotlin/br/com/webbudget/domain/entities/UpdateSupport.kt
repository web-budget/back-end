package br.com.webbudget.domain.entities

interface UpdateSupport<T : PersistentEntity<Long>> {

    fun updateFields(source: T): T
}
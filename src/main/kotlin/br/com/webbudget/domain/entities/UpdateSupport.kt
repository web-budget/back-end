package br.com.webbudget.domain.entities

interface UpdateSupport<S, T : PersistentEntity<Long>> {

    fun updateFields(source: S): T
}

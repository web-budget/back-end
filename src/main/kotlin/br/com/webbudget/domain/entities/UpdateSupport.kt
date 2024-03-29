package br.com.webbudget.domain.entities

fun interface UpdateSupport<S, T : PersistentEntity<Long>> {

    fun updateFields(source: S): T
}

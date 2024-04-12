package br.com.webbudget.domain.entities

@Deprecated("Use mapstruct instead")
fun interface UpdateSupport<S, T : PersistentEntity<Long>> {

    fun updateFields(source: S): T
}

package br.com.webbudget.domain.validators

interface BusinessValidator<T> {

    fun validate(value: T)

    companion object {
        const val ON_CREATE = "on_create"
        const val ON_UPDATE = "on_update"
        const val ON_DELETE = "on_delete"
    }
}

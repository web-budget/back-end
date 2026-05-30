package br.com.webbudget.infrastructure.utilities

import br.com.webbudget.domain.exceptions.DomainException

inline fun ensure(value: Boolean, lazyException: () -> DomainException) {
    if (!value) {
        throw lazyException()
    }
}
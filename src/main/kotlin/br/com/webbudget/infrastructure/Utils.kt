package br.com.webbudget.infrastructure

import br.com.webbudget.domain.exceptions.BusinessException

inline fun ensure(value: Boolean, lazyException: () -> BusinessException) {
    if (!value) {
        throw lazyException()
    }
}
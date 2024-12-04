package br.com.webbudget.infrastructure.utilities

import br.com.webbudget.domain.exceptions.BusinessException

inline fun ensure(value: Boolean, lazyException: () -> BusinessException) {
    if (!value) {
        throw lazyException()
    }
}
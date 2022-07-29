package br.com.webbudget.domain.exceptions

open class BusinessException(message: String, val detail: String) : RuntimeException(message)
package br.com.webbudget.domain.exceptions

open class BusinessException(message: String, val key: String, val parameters: Map<String, Any?>? = null) :
    RuntimeException(message)

package br.com.webbudget.application.payloads

import org.springframework.data.jpa.domain.Specification

fun interface SpecificationSupport<T : Any> {

    fun toSpecification(): Specification<T>
}

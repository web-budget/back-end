package br.com.webbudget.application.payloads

import org.springframework.data.jpa.domain.Specification

interface SpecificationSupport<T> {

    fun toSpecification(): Specification<T>
}

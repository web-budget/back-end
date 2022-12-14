package br.com.webbudget.application.payloads.support

import org.springframework.data.jpa.domain.Specification
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root

interface SpecificationSupport<T> {

    @Suppress("SpreadOperator")
    fun toSpecification(): Specification<T> {
        return Specification<T> { root, query, builder ->
            builder.and(*buildPredicates(root, query, builder).toTypedArray())
        }
    }

    fun buildPredicates(root: Root<T>, query: CriteriaQuery<*>, builder: CriteriaBuilder): List<Predicate>

    fun likeIgnoringCase(text: String) = "%${text.lowercase()}%"
}

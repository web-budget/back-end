package br.com.webbudget.application.payloads

import org.springframework.data.jpa.domain.Specification
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

interface SpecificationSupport<T> {

    fun toSpecification(): Specification<T> {
        return Specification<T> { root, query, builder ->
            builder.and(*buildPredicates(root, query, builder).toTypedArray())
        }
    }

    fun buildPredicates(root: Root<T>, query: CriteriaQuery<*>, builder: CriteriaBuilder): List<Predicate>

    fun likeIgnoringCase(text: String) = "%${text.lowercase()}%"
}

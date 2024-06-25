package br.com.webbudget.infrastructure.repository.registration

import br.com.webbudget.domain.entities.registration.FinancialPeriod
import br.com.webbudget.infrastructure.repository.BaseRepository
import br.com.webbudget.infrastructure.repository.SpecificationHelpers
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Repository

@Repository
interface FinancialPeriodRepository : BaseRepository<FinancialPeriod> {

    object Specifications : SpecificationHelpers {

        fun byName(value: String?) = Specification<FinancialPeriod> { root, _, builder ->
            value?.let { builder.like(builder.lower(root["name"]), likeIgnoreCase(value)) }
        }
    }
}
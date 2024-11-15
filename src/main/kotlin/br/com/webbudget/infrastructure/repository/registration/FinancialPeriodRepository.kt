package br.com.webbudget.infrastructure.repository.registration

import br.com.webbudget.domain.entities.registration.FinancialPeriod
import br.com.webbudget.domain.entities.registration.FinancialPeriod.Status
import br.com.webbudget.infrastructure.repository.BaseRepository
import br.com.webbudget.infrastructure.repository.SpecificationHelpers
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.UUID

@Repository
interface FinancialPeriodRepository : BaseRepository<FinancialPeriod> {

    fun findByNameIgnoreCase(name: String): FinancialPeriod?

    fun findByStatus(status: Status, pageable: Pageable): Page<FinancialPeriod>

    fun findByNameIgnoreCaseAndExternalIdNot(description: String, externalId: UUID): FinancialPeriod?

    @Query(
        """
         from FinancialPeriod fp
         where fp.status in ('ACTIVE', 'ENDED')
         and :startingAt between fp.startingAt and fp.endingAt or :endingAt between fp.startingAt and fp.endingAt 
    """
    )
    fun findByStartAndEndDates(startingAt: LocalDate, endingAt: LocalDate): List<FinancialPeriod>

    @Query(
        """
         from FinancialPeriod fp
         where fp.status in ('ACTIVE', 'ENDED')
         and (:startingAt between fp.startingAt and fp.endingAt or :endingAt between fp.startingAt and fp.endingAt)
         and fp.externalId <> :externalId
    """
    )
    fun findByStartAndEndDatesAndExternalIdNot(
        startingAt: LocalDate,
        endingAt: LocalDate,
        externalId: UUID
    ): List<FinancialPeriod>

    fun findByExternalIdAndStatusIn(externalId: UUID, statuses: List<Status>): FinancialPeriod?

    object Specifications : SpecificationHelpers {

        fun byName(value: String?) = Specification<FinancialPeriod> { root, _, builder ->
            value?.let { builder.like(builder.lower(root["name"]), likeIgnoreCase(value)) }
        }
    }
}
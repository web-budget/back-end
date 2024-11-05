package br.com.webbudget.infrastructure.repository.financial

import br.com.webbudget.application.payloads.financial.PeriodMovementFilter
import br.com.webbudget.domain.entities.financial.PeriodMovement
import br.com.webbudget.infrastructure.repository.BaseRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface PeriodMovementRepository : BaseRepository<PeriodMovement> {

    @Query(
        """
        from PeriodMovement pm 
            left join Apportionment ap on ap.periodMovement.id = pm.id
        where (:#{#filter.filter} is null 
                or pm.value = :#{#filter.decimalValue()}
                or lower(pm.name) like lower(concat('%', :#{#filter.filter}, '%') )
              )
        and (:#{#filter.states} is null or pm.state in :#{#filter.states})
        and (:#{#filter.financialPeriods} is null or pm.financialPeriod.externalId in :#{#filter.financialPeriods})
        and (:#{#filter.movementClass} is null or ap.movementClass.externalId = :#{#filter.movementClass})
        and (:#{#filter.costCenter} is null or ap.movementClass.costCenter.externalId = :#{#filter.costCenter})
        """
    )
    fun findByFilter(filter: PeriodMovementFilter, pageable: Pageable): Page<PeriodMovement>

    @EntityGraph(attributePaths = ["apportionments"])
    override fun findByExternalId(uuid: UUID): PeriodMovement?
}
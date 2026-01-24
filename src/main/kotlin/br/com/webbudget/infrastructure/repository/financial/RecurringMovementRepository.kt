package br.com.webbudget.infrastructure.repository.financial

import br.com.webbudget.application.payloads.financial.RecurringMovementFilter
import br.com.webbudget.domain.entities.financial.RecurringMovement
import br.com.webbudget.infrastructure.repository.BaseRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface RecurringMovementRepository : BaseRepository<RecurringMovement> {

    @Query(
        """
        from RecurringMovement rm 
            left join Classification cl 
        where (:#{#filter.filter} is null 
                or rm.value = :#{#filter.decimalValue()}
                or lower(rm.name) like lower(concat('%', :#{#filter.filter}, '%') )
              )
        and (:#{#filter.states} is null or rm.state in :#{#filter.states})
        and (:#{#filter.classification} is null or cl.externalId = :#{#filter.classification})
        and (:#{#filter.costCenter} is null or cl.costCenter.externalId = :#{#filter.costCenter})
        """
    )
    fun findByFilter(filter: RecurringMovementFilter, pageable: Pageable): Page<RecurringMovement>

    fun findByExternalIdAndState(externalId: UUID, state: RecurringMovement.State): RecurringMovement?
}
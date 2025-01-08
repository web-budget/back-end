package br.com.webbudget.infrastructure.repository.financial

import br.com.webbudget.application.payloads.financial.RecurringMovementFilter
import br.com.webbudget.domain.entities.financial.RecurringMovement
import br.com.webbudget.infrastructure.repository.BaseRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface RecurringMovementRepository : BaseRepository<RecurringMovement> {

    @Query(
        """
        from RecurringMovement rm 
            left join Apportionment ap on ap.recurringMovement.id = rm.id
        where (:#{#filter.filter} is null 
                or rm.value = :#{#filter.decimalValue()}
                or lower(rm.name) like lower(concat('%', :#{#filter.filter}, '%') )
              )
        and (:#{#filter.states} is null or rm.state in :#{#filter.states})
        and (:#{#filter.movementClass} is null or ap.movementClass.externalId = :#{#filter.movementClass})
        and (:#{#filter.costCenter} is null or ap.movementClass.costCenter.externalId = :#{#filter.costCenter})
        """
    )
    fun findByFilter(filter: RecurringMovementFilter, pageable: Pageable): Page<RecurringMovement>

    @EntityGraph(attributePaths = ["apportionments"])
    override fun findByExternalId(uuid: UUID): RecurringMovement?
}
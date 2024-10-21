package br.com.webbudget.infrastructure.repository.financial

import br.com.webbudget.application.payloads.financial.PeriodMovementFilter
import br.com.webbudget.domain.entities.financial.PeriodMovement
import br.com.webbudget.infrastructure.repository.BaseRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query

interface PeriodMovementRepository : BaseRepository<PeriodMovement> {

    @Query(
        """
        from PeriodMovement 
        """
    )
    fun findByFilter(filter: PeriodMovementFilter, pageable: Pageable): Page<PeriodMovement>
}
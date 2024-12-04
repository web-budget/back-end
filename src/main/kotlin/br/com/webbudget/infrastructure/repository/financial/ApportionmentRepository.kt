package br.com.webbudget.infrastructure.repository.financial

import br.com.webbudget.domain.entities.financial.Apportionment
import br.com.webbudget.infrastructure.repository.BaseRepository
import java.util.UUID

interface ApportionmentRepository : BaseRepository<Apportionment> {

    fun findByPeriodMovementExternalId(periodMovementExternalId: UUID): List<Apportionment>

    fun deleteByPeriodMovementExternalId(periodMovementExternalId: UUID)
}
package br.com.webbudget.services.financial

import br.com.webbudget.BaseIntegrationTest
import br.com.webbudget.domain.services.financial.PeriodMovementService
import org.junit.jupiter.api.Disabled
import org.springframework.beans.factory.annotation.Autowired

@Disabled
@Suppress("unused")
class PeriodMovementServiceITest : BaseIntegrationTest() {

    @Autowired
    private lateinit var periodMovementService: PeriodMovementService
}
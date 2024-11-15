package br.com.webbudget.controllers.financial

import br.com.webbudget.BaseControllerIntegrationTest
import br.com.webbudget.application.controllers.financial.PeriodMovementController
import br.com.webbudget.application.mappers.financial.ApportionmentMapper
import br.com.webbudget.application.mappers.financial.PeriodMovementMapperImpl
import br.com.webbudget.application.mappers.registration.FinancialPeriodMapper
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.get

@Disabled
@WebMvcTest(PeriodMovementController::class)
@Import(value = [PeriodMovementMapperImpl::class, ApportionmentMapper::class, FinancialPeriodMapper::class])
class PeriodMovementControllerUTest : BaseControllerIntegrationTest() {

    @Test
    fun `should require authorization`() {
        mockMvc.get(ENDPOINT_URL) {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isUnauthorized() }
        }
    }

    companion object {
        private const val ENDPOINT_URL = "/api/financial/period-movements"
    }
}
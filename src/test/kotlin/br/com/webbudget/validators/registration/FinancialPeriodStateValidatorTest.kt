package br.com.webbudget.validators.registration

import br.com.webbudget.domain.entities.registration.FinancialPeriod.Status
import br.com.webbudget.domain.exceptions.BusinessException
import br.com.webbudget.domain.validators.registration.FinancialPeriodStateValidator
import br.com.webbudget.infrastructure.repository.registration.FinancialPeriodRepository
import br.com.webbudget.utilities.fixture.createFinancialPeriod
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThatNoException
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.UUID

@ExtendWith(MockKExtension::class)
class FinancialPeriodStateValidatorTest {

    @MockK
    private lateinit var financialPeriodRepository: FinancialPeriodRepository

    @InjectMockKs
    private lateinit var validator: FinancialPeriodStateValidator

    @Test
    fun `should pass if state permit changes`() {

        val savedPeriod = createFinancialPeriod(id = 1L)
        val periodToValidate = createFinancialPeriod(id = 1L)

        every {
            financialPeriodRepository.findByExternalIdAndStatusIn(any<UUID>(), any<List<Status>>())
        } returns savedPeriod

        assertThatNoException()
            .isThrownBy { validator.validate(periodToValidate) }

        verify(exactly = 1) {
            financialPeriodRepository.findByExternalIdAndStatusIn(ofType<UUID>(), ofType<List<Status>>())
        }

        confirmVerified(financialPeriodRepository)
    }

    @Test
    fun `should fail if state doesn't permit changes`() {

        val periodToValidate = createFinancialPeriod(id = 1L, status = Status.ACTIVE)

        every {
            financialPeriodRepository.findByExternalIdAndStatusIn(any<UUID>(), any<List<Status>>())
        } returns null

        assertThatThrownBy { validator.validate(periodToValidate) }
            .isInstanceOf(BusinessException::class.java)
            .hasMessage("You can't delete or update non open periods")

        verify(exactly = 1) {
            financialPeriodRepository.findByExternalIdAndStatusIn(ofType<UUID>(), ofType<List<Status>>())
        }

        confirmVerified(financialPeriodRepository)
    }
}
package br.com.webbudget.validators.registration

import br.com.webbudget.domain.exceptions.BusinessException
import br.com.webbudget.domain.validators.registration.FinancialPeriodDatesOverlapValidator
import br.com.webbudget.infrastructure.repository.registration.FinancialPeriodRepository
import br.com.webbudget.utilities.fixtures.createFinancialPeriod
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
import java.time.LocalDate
import java.util.UUID

@ExtendWith(MockKExtension::class)
class FinancialPeriodDatesOverlapValidatorTest {

    @MockK
    private lateinit var financialPeriodRepository: FinancialPeriodRepository

    @InjectMockKs
    private lateinit var validator: FinancialPeriodDatesOverlapValidator

    @Test
    fun `should pass if dates didn't overlap with any active period`() {

        val financialPeriod = createFinancialPeriod()

        every {
            financialPeriodRepository.findByStartAndEndDates(any<LocalDate>(), any<LocalDate>())
        } returns emptyList()

        assertThatNoException()
            .isThrownBy { validator.validate(financialPeriod) }

        verify(exactly = 1) {
            financialPeriodRepository.findByStartAndEndDates(ofType<LocalDate>(), ofType<LocalDate>())
        }

        confirmVerified(financialPeriodRepository)
    }

    @Test
    fun `should pass if saved period dates didn't overlap with any active period`() {

        val financialPeriod = createFinancialPeriod(id = 1L)

        every {
            financialPeriodRepository.findByStartAndEndDatesAndExternalIdNot(
                any<LocalDate>(),
                any<LocalDate>(),
                any<UUID>()
            )
        } returns emptyList()

        assertThatNoException()
            .isThrownBy { validator.validate(financialPeriod) }

        verify(exactly = 1) {
            financialPeriodRepository.findByStartAndEndDatesAndExternalIdNot(
                ofType<LocalDate>(),
                ofType<LocalDate>(),
                ofType<UUID>()
            )
        }

        confirmVerified(financialPeriodRepository)
    }

    @Test
    fun `should fail if dates overlap with any active period`() {

        val financialPeriod = createFinancialPeriod(name = "10/2024")
        val financialPeriods = listOf(createFinancialPeriod(name = "08/2024"))

        every {
            financialPeriodRepository.findByStartAndEndDates(any<LocalDate>(), any<LocalDate>())
        } returns financialPeriods

        assertThatThrownBy { validator.validate(financialPeriod) }
            .isInstanceOf(BusinessException::class.java)

        verify(exactly = 1) {
            financialPeriodRepository.findByStartAndEndDates(ofType<LocalDate>(), ofType<LocalDate>())
        }

        confirmVerified(financialPeriodRepository)
    }

    @Test
    fun `should fail if saved period dates overlap with any active period`() {

        val financialPeriod = createFinancialPeriod(id = 1L, name = "10/2024")
        val financialPeriods = listOf(createFinancialPeriod(id = 2L, name = "08/2024"))

        every {
            financialPeriodRepository.findByStartAndEndDatesAndExternalIdNot(
                any<LocalDate>(),
                any<LocalDate>(),
                any<UUID>()
            )
        } returns financialPeriods

        assertThatThrownBy { validator.validate(financialPeriod) }
            .isInstanceOf(BusinessException::class.java)

        verify(exactly = 1) {
            financialPeriodRepository.findByStartAndEndDatesAndExternalIdNot(
                ofType<LocalDate>(),
                ofType<LocalDate>(),
                ofType<UUID>()
            )
        }

        confirmVerified(financialPeriodRepository)
    }
}
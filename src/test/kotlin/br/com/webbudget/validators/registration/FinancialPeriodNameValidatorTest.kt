package br.com.webbudget.validators.registration

import br.com.webbudget.domain.exceptions.DuplicatedPropertyException
import br.com.webbudget.domain.validators.registration.FinancialPeriodNameValidator
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
class FinancialPeriodNameValidatorTest {

    @MockK
    private lateinit var financialPeriodRepository: FinancialPeriodRepository

    @InjectMockKs
    private lateinit var validator: FinancialPeriodNameValidator

    @Test
    fun `should pass if name is valid`() {

        val financialPeriod = createFinancialPeriod()

        every { financialPeriodRepository.findByNameIgnoreCase(any<String>()) } returns null

        assertThatNoException()
            .isThrownBy { validator.validate(financialPeriod) }

        verify(exactly = 1) { financialPeriodRepository.findByNameIgnoreCase(ofType<String>()) }

        confirmVerified(financialPeriodRepository)
    }

    @Test
    fun `should pass if name is valid for a saved period`() {

        val financialPeriod = createFinancialPeriod(id = 1L)

        every {
            financialPeriodRepository.findByNameIgnoreCaseAndExternalIdNot(any<String>(), any<UUID>())
        } returns null

        assertThatNoException()
            .isThrownBy { validator.validate(financialPeriod) }

        verify(exactly = 1) {
            financialPeriodRepository.findByNameIgnoreCaseAndExternalIdNot(ofType<String>(), ofType<UUID>())
        }

        confirmVerified(financialPeriodRepository)
    }

    @Test
    fun `should fail if name is invalid`() {

        val financialPeriod = createFinancialPeriod()
        val conflictingPeriod = createFinancialPeriod(id = 2L)

        every { financialPeriodRepository.findByNameIgnoreCase(any<String>()) } returns conflictingPeriod

        assertThatThrownBy { validator.validate(financialPeriod) }
            .isInstanceOf(DuplicatedPropertyException::class.java)
            .hasMessage("financial-period.errors.duplicated-name")

        verify(exactly = 1) { financialPeriodRepository.findByNameIgnoreCase(ofType<String>()) }

        confirmVerified(financialPeriodRepository)
    }

    @Test
    fun `should fail if name is invalid for a saved period`() {

        val financialPeriod = createFinancialPeriod(id = 1L)
        val conflictingPeriod = createFinancialPeriod(id = 2L)

        every {
            financialPeriodRepository.findByNameIgnoreCaseAndExternalIdNot(any<String>(), any<UUID>())
        } returns conflictingPeriod

        assertThatThrownBy { validator.validate(financialPeriod) }
            .isInstanceOf(DuplicatedPropertyException::class.java)
            .hasMessage("financial-period.errors.duplicated-name")

        verify(exactly = 1) {
            financialPeriodRepository.findByNameIgnoreCaseAndExternalIdNot(ofType<String>(), ofType<UUID>())
        }

        confirmVerified(financialPeriodRepository)
    }
}
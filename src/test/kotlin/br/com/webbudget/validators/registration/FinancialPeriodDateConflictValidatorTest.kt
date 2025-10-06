package br.com.webbudget.validators.registration

import br.com.webbudget.domain.exceptions.BusinessException
import br.com.webbudget.domain.validators.registration.FinancialPeriodDateConflictValidator
import br.com.webbudget.utilities.fixtures.createFinancialPeriod
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThatNoException
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate

@ExtendWith(MockKExtension::class)
class FinancialPeriodDateConflictValidatorTest {

    @InjectMockKs
    private lateinit var validator: FinancialPeriodDateConflictValidator

    @Test
    fun `should pass if dates are valid`() {

        val financialPeriod = createFinancialPeriod(
            startingAt = LocalDate.now(),
            endingAt = LocalDate.now().plusDays(15)
        )

        assertThatNoException()
            .isThrownBy { validator.validate(financialPeriod) }
    }

    @Test
    fun `should fail if start date is before end date`() {

        val financialPeriod = createFinancialPeriod(
            startingAt = LocalDate.now(),
            endingAt = LocalDate.now().minusDays(15)
        )

        assertThatThrownBy { validator.validate(financialPeriod) }
            .isInstanceOf(BusinessException::class.java)
            .hasMessage("Start date must be before end date")
    }
}
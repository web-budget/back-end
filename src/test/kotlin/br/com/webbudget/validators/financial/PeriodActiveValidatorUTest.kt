package br.com.webbudget.validators.financial

import br.com.webbudget.domain.entities.registration.FinancialPeriod
import br.com.webbudget.domain.entities.registration.FinancialPeriod.Status.ACCOUNTED
import br.com.webbudget.domain.entities.registration.FinancialPeriod.Status.ACTIVE
import br.com.webbudget.domain.entities.registration.FinancialPeriod.Status.ENDED
import br.com.webbudget.domain.exceptions.BusinessException
import br.com.webbudget.domain.validators.financial.PeriodActiveValidator
import br.com.webbudget.utilities.fixture.createFinancialPeriod
import br.com.webbudget.utilities.fixture.createPeriodMovement
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThatNoException
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@ExtendWith(MockKExtension::class)
class PeriodActiveValidatorUTest {

    @InjectMockKs
    private lateinit var periodActiveValidator: PeriodActiveValidator

    @Test
    fun `should throw exception when period is not open`() {

        val movement = createPeriodMovement(financialPeriod = createFinancialPeriod(status = ACCOUNTED))

        assertThatThrownBy { periodActiveValidator.validate(movement) }
            .isInstanceOf(BusinessException::class.java)
            .withFailMessage { "Financial period is not open" }
    }

    @ParameterizedTest
    @MethodSource("financialPeriodTestData")
    fun `should not throw exception when period is open`(financialPeriod: FinancialPeriod) {

        val movement = createPeriodMovement(financialPeriod = financialPeriod)

        assertThatNoException().isThrownBy { periodActiveValidator.validate(movement) }
    }

    companion object {

        @JvmStatic
        fun financialPeriodTestData(): Stream<Arguments> = Stream.of(
            Arguments.of(createFinancialPeriod(status = ACTIVE)),
            Arguments.of(createFinancialPeriod(status = ENDED))
        )
    }
}
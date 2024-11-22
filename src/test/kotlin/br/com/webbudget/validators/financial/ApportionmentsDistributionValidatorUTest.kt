package br.com.webbudget.validators.financial

import br.com.webbudget.domain.exceptions.BusinessException
import br.com.webbudget.domain.validators.financial.ApportionmentsDistributionValidator
import br.com.webbudget.utilities.fixture.createApportionment
import br.com.webbudget.utilities.fixture.createPeriodMovement
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThatNoException
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal

@ExtendWith(MockKExtension::class)
class ApportionmentsDistributionValidatorUTest {

    @InjectMockKs
    private lateinit var apportionmentsDistributionValidator: ApportionmentsDistributionValidator

    @Test
    fun `should not thrown exception if apportionments distribution is valid`() {

        val movement = createPeriodMovement()

        assertThatNoException().isThrownBy { apportionmentsDistributionValidator.validate(movement) }
    }

    @Test
    fun `should thrown exception if apportionments distribution is invalid`() {

        val movement = createPeriodMovement(apportionments = mutableListOf(createApportionment(value = BigDecimal.TEN)))

        assertThatThrownBy { apportionmentsDistributionValidator.validate(movement) }
            .isInstanceOf(BusinessException::class.java)
            .withFailMessage { "Apportionments total must be equal to movement value" }
    }
}
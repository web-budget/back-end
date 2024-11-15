package br.com.webbudget.validators.financial

import br.com.webbudget.domain.entities.financial.PeriodMovement
import br.com.webbudget.domain.entities.financial.PeriodMovement.State.ACCOUNTED
import br.com.webbudget.domain.entities.financial.PeriodMovement.State.OPEN
import br.com.webbudget.domain.entities.financial.PeriodMovement.State.PAID
import br.com.webbudget.domain.exceptions.BusinessException
import br.com.webbudget.domain.validators.financial.PeriodMovementStateValidator
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
class PeriodMovementStateValidatorUTest {

    @InjectMockKs
    private lateinit var periodMovementStateValidator: PeriodMovementStateValidator

    @Test
    fun `should throw exception when movement is not open`() {

        val movement = createPeriodMovement(state = ACCOUNTED)

        assertThatThrownBy { periodMovementStateValidator.validate(movement) }
            .isInstanceOf(BusinessException::class.java)
            .withFailMessage { "Period movement is not open" }
    }

    @ParameterizedTest
    @MethodSource("periodMovementTestData")
    fun `should not throw exception when period is open`(movement: PeriodMovement) {
        assertThatNoException().isThrownBy { periodMovementStateValidator.validate(movement) }
    }

    companion object {

        @JvmStatic
        fun periodMovementTestData(): Stream<Arguments> = Stream.of(
            Arguments.of(createPeriodMovement(state = OPEN)),
            Arguments.of(createPeriodMovement(state = PAID))
        )
    }
}
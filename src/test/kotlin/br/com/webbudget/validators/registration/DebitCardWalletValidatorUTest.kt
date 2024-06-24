package br.com.webbudget.validators.registration

import br.com.webbudget.domain.entities.registration.Card
import br.com.webbudget.domain.exceptions.BusinessException
import br.com.webbudget.domain.validators.registration.DebitCardWalletValidator
import br.com.webbudget.utilities.fixture.createCard
import br.com.webbudget.utilities.fixture.createWallet
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.api.AssertionsForClassTypes.assertThatNoException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class DebitCardWalletValidatorUTest {

    @InjectMockKs
    private lateinit var debitCardWalletValidator: DebitCardWalletValidator

    @Test
    fun `should pass when debit card has wallet`() {

        val card = createCard(type = Card.Type.DEBIT, wallet = createWallet())

        assertThatNoException()
            .isThrownBy { debitCardWalletValidator.validate(card) }
    }

    @Test
    fun `should thrown exception when debit card has no wallet`() {

        val card = createCard(type = Card.Type.DEBIT, wallet = null)

        assertThatThrownBy { debitCardWalletValidator.validate(card) }
            .isInstanceOf(BusinessException::class.java)
    }
}
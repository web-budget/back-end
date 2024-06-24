package br.com.webbudget.validators.registration

import br.com.webbudget.domain.exceptions.DuplicatedPropertyException
import br.com.webbudget.domain.validators.registration.BankingInformationValidator
import br.com.webbudget.infrastructure.repository.registration.WalletRepository
import br.com.webbudget.utilities.fixture.createWallet
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
class BankingInformationValidatorUTest {

    @MockK
    private lateinit var walletRepository: WalletRepository

    @InjectMockKs
    private lateinit var bankingInformationValidator: BankingInformationValidator

    @Test
    fun `should fail for different entities with same banking information`() {

        every { walletRepository.findByBankInfo("Bank", "123", "456789") } returns createWallet()

        val toValidate = createWallet(id = null, externalId = null)

        assertThatThrownBy { bankingInformationValidator.validate(toValidate) }
            .isInstanceOf(DuplicatedPropertyException::class.java)
            .hasMessage("wallet.errors.duplicated-bank-info")

        verify(exactly = 1) { walletRepository.findByBankInfo("Bank", "123", "456789") }

        confirmVerified(walletRepository)
    }

    @Test
    fun `should not fail if entities are equal`() {

        val externalId = UUID.randomUUID()
        val toValidate = createWallet(id = 1L, externalId = externalId)

        every {
            walletRepository.findByBankInfo("Bank", "123", "456789", externalId)
        } returns null

        assertThatNoException()
            .isThrownBy { bankingInformationValidator.validate(toValidate) }

        verify(exactly = 1) {
            walletRepository.findByBankInfo("Bank", "123", "456789", externalId)
        }

        confirmVerified(walletRepository)
    }

    @Test
    fun `should not validate if no valid bank information`() {

        val externalId = UUID.randomUUID()
        val toValidate = createWallet(id = 1L, externalId = externalId, agency = null)

        assertThatNoException()
            .isThrownBy { bankingInformationValidator.validate(toValidate) }

        confirmVerified(walletRepository)
    }
}

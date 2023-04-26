package br.com.webbudget.validators.registration

import br.com.webbudget.domain.entities.registration.Wallet.Type.BANK_ACCOUNT
import br.com.webbudget.domain.exceptions.DuplicatedPropertyException
import br.com.webbudget.domain.validators.registration.BankingInformationValidator
import br.com.webbudget.infrastructure.repository.registration.WalletRepository
import br.com.webbudget.utilities.fixture.WalletFixture.create
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThatNoException
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.UUID

@ExtendWith(MockKExtension::class)
class BankingInformationValidatorTest {

    @MockK
    private lateinit var walletRepository: WalletRepository

    @InjectMockKs
    private lateinit var bankingInformationValidator: BankingInformationValidator

    @Test
    fun `should fail for different entities with same banking information`() {

        val duplicated = create("Duplicated", BANK_ACCOUNT, "1", "1", "1")
            .apply {
                this.id = 1L
                this.externalId = UUID.randomUUID()
            }

        every { walletRepository.findByBankInfo("1", "1", "1") } returns duplicated

        val toValidate = create("Duplicated", BANK_ACCOUNT, "1", "1", "1")

        assertThatThrownBy { bankingInformationValidator.validate(toValidate) }
            .isInstanceOf(DuplicatedPropertyException::class.java)
            .hasMessage("wallet.errors.duplicated-bank-info")

        verify(exactly = 1) { walletRepository.findByBankInfo("1", "1", "1") }
    }

    @Test
    fun `should not fail if entities are equal`() {

        val externalId = UUID.randomUUID()

        val notDuplicated = create("Not duplicated", BANK_ACCOUNT, "1", "1", "1")
            .apply {
                this.id = 1L
                this.externalId = externalId
            }

        every {
            walletRepository.findByBankInfo("1", "1", "1", externalId)
        } returns null

        assertThatNoException()
            .isThrownBy { bankingInformationValidator.validate(notDuplicated) }

        verify(exactly = 1) {
            walletRepository.findByBankInfo("1", "1", "1", externalId)
        }
    }

    @Test
    @Disabled
    fun `should not validate if no valid bank information`() {

    }
}

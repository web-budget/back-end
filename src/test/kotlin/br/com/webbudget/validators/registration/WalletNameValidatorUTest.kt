package br.com.webbudget.validators.registration

import br.com.webbudget.domain.exceptions.DuplicatedPropertyException
import br.com.webbudget.domain.validators.registration.WalletNameValidator
import br.com.webbudget.infrastructure.repository.registration.WalletRepository
import br.com.webbudget.utilities.fixture.createWallet
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.AssertionsForClassTypes.assertThatNoException
import org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.UUID

@ExtendWith(MockKExtension::class)
class WalletNameValidatorUTest {

    @MockK
    private lateinit var walletRepository: WalletRepository

    @InjectMockKs
    private lateinit var walletNameValidator: WalletNameValidator

    @Test
    fun `should fail for different entities and equal name`() {

        every {
            walletRepository.findByNameIgnoreCase("Wallet")
        } returns createWallet()

        val toValidate = createWallet(id = null, externalId = null)

        assertThatThrownBy { walletNameValidator.validate(toValidate) }
            .isInstanceOf(DuplicatedPropertyException::class.java)
            .hasMessage("wallet.errors.duplicated-name")

        verify(exactly = 1) { walletRepository.findByNameIgnoreCase("Wallet") }

        confirmVerified(walletRepository)
    }

    @Test
    fun `should not fail if entities are equal`() {

        val externalId = UUID.randomUUID()
        val toValidate = createWallet(id = 1L, externalId = externalId)

        every {
            walletRepository.findByNameIgnoreCaseAndExternalIdNot("Wallet", externalId)
        } returns null

        assertThatNoException()
            .isThrownBy { walletNameValidator.validate(toValidate) }

        verify(exactly = 1) {
            walletRepository.findByNameIgnoreCaseAndExternalIdNot("Wallet", externalId)
        }

        confirmVerified(walletRepository)
    }
}

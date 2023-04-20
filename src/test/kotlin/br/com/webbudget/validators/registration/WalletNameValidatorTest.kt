package br.com.webbudget.validators.registration

import br.com.webbudget.domain.entities.registration.Wallet.Type.PERSONAL
import br.com.webbudget.domain.exceptions.DuplicatedPropertyException
import br.com.webbudget.domain.validators.registration.WalletNameValidator
import br.com.webbudget.infrastructure.repository.registration.WalletRepository
import br.com.webbudget.utilities.fixture.WalletFixture.create
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
class WalletNameValidatorTest {

    @MockK
    private lateinit var walletRepository: WalletRepository

    @InjectMockKs
    private lateinit var walletNameValidator: WalletNameValidator

    @Test
    fun `should fail for different entities and equal name`() {

        val duplicated = create("Duplicated", PERSONAL)
            .apply {
                this.id = 1L
                this.externalId = UUID.randomUUID()
            }

        every {
            walletRepository.findByNameIgnoreCase("Duplicated")
        } returns duplicated

        val toValidate = create("Duplicated", PERSONAL)

        assertThatThrownBy { walletNameValidator.validate(toValidate) }
            .isInstanceOf(DuplicatedPropertyException::class.java)
            .hasMessage("wallet.errors.duplicated-name")

        verify(exactly = 1) { walletRepository.findByNameIgnoreCase("Duplicated") }
    }

    @Test
    fun `should not fail if entities are equal`() {

        val externalId = UUID.randomUUID()

        val notDuplicated = create("Not duplicated", PERSONAL)
            .apply {
                this.id = 1L
                this.externalId = externalId
            }

        every {
            walletRepository.findByNameIgnoreCaseAndExternalIdNot("Not duplicated", externalId)
        } returns null

        assertThatNoException()
            .isThrownBy { walletNameValidator.validate(notDuplicated) }

        verify(exactly = 1) {
            walletRepository.findByNameIgnoreCaseAndExternalIdNot("Not duplicated", externalId)
        }
    }
}

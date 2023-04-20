package br.com.webbudget.services.registration

import br.com.webbudget.BaseIntegrationTest
import br.com.webbudget.application.payloads.registration.WalletUpdateForm
import br.com.webbudget.domain.entities.registration.Wallet
import br.com.webbudget.domain.entities.registration.Wallet.Type.BANK_ACCOUNT
import br.com.webbudget.domain.entities.registration.Wallet.Type.INVESTMENT
import br.com.webbudget.domain.entities.registration.Wallet.Type.PERSONAL
import br.com.webbudget.domain.services.registration.WalletService
import br.com.webbudget.domain.services.registration.WalletValidationService
import br.com.webbudget.infrastructure.repository.registration.WalletRepository
import br.com.webbudget.utilities.fixture.WalletFixture.create
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import java.math.BigDecimal
import java.util.UUID

class WalletServiceTest : BaseIntegrationTest() {

    @MockkBean
    private lateinit var walletValidationService: WalletValidationService

    @Autowired
    private lateinit var walletService: WalletService

    @Autowired
    private lateinit var walletRepository: WalletRepository

    @ParameterizedTest
    @MethodSource("buildCreateParams")
    fun `should save when validation pass`(toCreate: Wallet) {

        every { walletValidationService.validateOnCreate(any()) } just runs

        val externalId = walletService.create(toCreate)

        val created = walletRepository.findByExternalId(externalId)
            ?: fail(OBJECT_NOT_FOUND_ERROR)

        assertThat(created)
            .satisfies({
                assertThat(it.id).isNotNull()
                assertThat(it.externalId).isNotNull()
                assertThat(it.version).isNotNull()
                assertThat(it.createdOn).isNotNull()
                assertThat(it.active).isEqualTo(toCreate.active)
                assertThat(it.name).isEqualTo(toCreate.name)
                assertThat(it.type).isEqualTo(toCreate.type)
                assertThat(it.currentBalance).isEqualByComparingTo(toCreate.currentBalance)
                assertThat(it.description).isEqualTo(toCreate.description)
                assertThat(it.bank).isEqualTo(toCreate.bank)
                assertThat(it.agency).isEqualTo(toCreate.agency)
                assertThat(it.number).isEqualTo(toCreate.number)
            })
    }

    @Test
    fun `should not save when validation fail`() {

        val toCreate = create()

        every { walletValidationService.validateOnCreate(any()) } throws
                RuntimeException("Oops, something went wrong!")

        assertThatThrownBy { walletService.create(toCreate) }
            .isInstanceOf(RuntimeException::class.java)
    }

    @ParameterizedTest
    @MethodSource("buildUpdateParams")
    fun `should update when validation pass`(toCreate: Wallet, updateForm: WalletUpdateForm) {

        every { walletValidationService.validateOnCreate(any()) } just runs
        every { walletValidationService.validateOnUpdate(any()) } just runs

        val externalId = walletService.create(toCreate)

        val toUpdate = walletRepository.findByExternalId(externalId)
            ?: fail(OBJECT_NOT_FOUND_ERROR)

        toUpdate.updateFields(updateForm)
        val updated = walletService.update(toUpdate)

        assertThat(updated)
            .isNotNull
            .satisfies({
                assertThat(it.id).isEqualTo(toUpdate.id)
                assertThat(it.externalId).isEqualTo(toUpdate.externalId)
                assertThat(it.version).isGreaterThan(toUpdate.version)
                assertThat(it.createdOn).isEqualTo(toUpdate.createdOn)
                assertThat(it.active).isEqualTo(toUpdate.active)
                assertThat(it.name).isEqualTo(toUpdate.name)
                assertThat(it.type).isEqualTo(toUpdate.type)
                assertThat(it.currentBalance).isEqualByComparingTo(toUpdate.currentBalance)
                assertThat(it.description).isEqualTo(toUpdate.description)
                assertThat(it.bank).isEqualTo(toUpdate.bank)
                assertThat(it.agency).isEqualTo(toUpdate.agency)
                assertThat(it.number).isEqualTo(toUpdate.number)
            })
    }

    @Test
    fun `should not update when validation fail`() {

        val toUpdate = create(1L, UUID.randomUUID(), "Wallet", PERSONAL)

        every { walletValidationService.validateOnCreate(any()) } throws
                RuntimeException("Oops, something went wrong!")

        assertThatThrownBy { walletService.update(toUpdate) }
            .isInstanceOf(RuntimeException::class.java)
    }

    @Test
    fun `should delete`() {

        every { walletValidationService.validateOnCreate(any()) } just runs
        every { walletValidationService.validateOnDelete(any()) } just runs

        val toCreate = create()
        val externalId = walletService.create(toCreate)

        val toDelete = walletRepository.findByExternalId(externalId)
            ?: fail(OBJECT_NOT_FOUND_ERROR)

        walletService.delete(toDelete)

        val deleted = walletRepository.findByExternalId(externalId)
        assertThat(deleted).isNull()
    }

    @Test
    @Disabled
    fun `should fail to delete when in use`() {
        // TODO do the logic to test constraint violation here
    }

    companion object {

        @JvmStatic
        fun buildUpdateParams() = listOf(
            Arguments.of(
                Wallet("Personal", PERSONAL, BigDecimal.ZERO, true, "Personal"),
                WalletUpdateForm("updated", false, "updated")
            ),
            Arguments.of(
                Wallet("Investments", INVESTMENT, BigDecimal.ONE, true, "Investments", "1", "1", "1"),
                WalletUpdateForm("updated", false, "updated", "0", "0", "0"),
            ),
            Arguments.of(
                Wallet("Bank", BANK_ACCOUNT, BigDecimal.TEN, true, "Bank account", "1", "1", "1"),
                WalletUpdateForm("updated", false, "updated", "0", "0", "0"),
            )
        )

        @JvmStatic
        fun buildCreateParams() = listOf(
            Arguments.of(Wallet("Personal", PERSONAL, BigDecimal.ZERO, true, "Personal")),
            Arguments.of(Wallet("Investments", INVESTMENT, BigDecimal.ONE, true, "Investments", "1", "1", "1")),
            Arguments.of(Wallet("Bank", BANK_ACCOUNT, BigDecimal.TEN, true, "Bank account", "1", "1", "1"))
        )
    }
}

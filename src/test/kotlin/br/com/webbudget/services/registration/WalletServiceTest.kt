package br.com.webbudget.services.registration

import br.com.webbudget.BaseIntegrationTest
import br.com.webbudget.application.payloads.registration.WalletUpdateForm
import br.com.webbudget.domain.entities.registration.Wallet
import br.com.webbudget.domain.entities.registration.Wallet.Type.BANK_ACCOUNT
import br.com.webbudget.domain.entities.registration.Wallet.Type.INVESTMENT
import br.com.webbudget.domain.entities.registration.Wallet.Type.PERSONAL
import br.com.webbudget.domain.exceptions.DuplicatedPropertyException
import br.com.webbudget.domain.services.registration.WalletService
import br.com.webbudget.infrastructure.repository.registration.WalletRepository
import br.com.webbudget.utilities.fixture.WalletFixture.create
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import java.math.BigDecimal
import java.util.UUID

class WalletServiceTest : BaseIntegrationTest() {

    @Autowired
    private lateinit var walletService: WalletService

    @Autowired
    private lateinit var walletRepository: WalletRepository

    @ParameterizedTest
    @MethodSource("buildCreateParams")
    @Sql("/sql/registration/clear-tables.sql", "/sql/registration/create-wallets.sql")
    fun `should save`(toCreate: Wallet) {

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
    @Sql("/sql/registration/clear-tables.sql", "/sql/registration/create-wallets.sql")
    fun `should not save when name is duplicated`() {

        val toCreate = create("Pessoal", PERSONAL)

        assertThatThrownBy { walletService.create(toCreate) }
            .isInstanceOf(DuplicatedPropertyException::class.java)
    }

    @Test
    @Sql("/sql/registration/clear-tables.sql", "/sql/registration/create-wallets.sql")
    fun `should not save when banking information is duplicated`() {

        val toCreate = create("Conta Bancaria", BANK_ACCOUNT, "Banco", "1", "1")

        assertThatThrownBy { walletService.create(toCreate) }
            .isInstanceOf(DuplicatedPropertyException::class.java)
    }

    @ParameterizedTest
    @MethodSource("buildUpdateParams")
    @Sql("/sql/registration/clear-tables.sql", "/sql/registration/create-wallets.sql")
    fun `should update`(idToUpdate: UUID, updateForm: WalletUpdateForm) {

        val toUpdate = walletRepository.findByExternalId(idToUpdate)
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
    @Sql("/sql/registration/clear-tables.sql", "/sql/registration/create-wallets.sql")
    fun `should not update when name is duplicated`() {

        val externalId = UUID.fromString("d6421251-7b38-4765-88e0-4d70bc3bc4c7")

        val toUpdate = walletRepository.findByExternalId(externalId)
            ?: fail(OBJECT_NOT_FOUND_ERROR)

        toUpdate.apply { this.name = "Conta bancaria" }

        assertThatThrownBy { walletService.update(toUpdate) }
            .isInstanceOf(DuplicatedPropertyException::class.java)
    }

    @Test
    @Sql("/sql/registration/clear-tables.sql", "/sql/registration/create-wallets.sql")
    fun `should not update when banking information is duplicated`() {

        val externalId = UUID.fromString("cd00845c-ae27-47e4-8282-c8df1c42acfe")

        val toUpdate = walletRepository.findByExternalId(externalId)
            ?: fail(OBJECT_NOT_FOUND_ERROR)

        toUpdate.apply {
            this.bank = "Corretora"
            this.agency = "1"
            this.number = "1"
        }

        assertThatThrownBy { walletService.update(toUpdate) }
            .isInstanceOf(DuplicatedPropertyException::class.java)
    }

    @Test
    fun `should delete`() {

        val externalId = UUID.fromString("d6421251-7b38-4765-88e0-4d70bc3bc4c7")

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
                UUID.fromString("d6421251-7b38-4765-88e0-4d70bc3bc4c7"),
                WalletUpdateForm("updated", false, "updated")
            ),
            Arguments.of(
                UUID.fromString("4ade8a17-460b-40fc-b200-1504bcd4aaf7"),
                WalletUpdateForm("updated", false, "updated", "updated", "2", "2"),
            ),
            Arguments.of(
                UUID.fromString("cd00845c-ae27-47e4-8282-c8df1c42acfe"),
                WalletUpdateForm("updated", false, "updated", "updated", "4", "4"),
            )
        )

        @JvmStatic
        fun buildCreateParams() = listOf(
            Arguments.of(Wallet("Personal", PERSONAL, BigDecimal.ZERO, true, "Personal")),
            Arguments.of(Wallet("Investments", INVESTMENT, BigDecimal.ONE, true, "Investments", "Bank", "1", "1")),
            Arguments.of(Wallet("Bank Account", BANK_ACCOUNT, BigDecimal.TEN, true, "Bank account", "Bank", "2", "2"))
        )
    }
}

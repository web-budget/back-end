package br.com.webbudget.services.registration

import br.com.webbudget.BaseIntegrationTest
import br.com.webbudget.domain.entities.registration.Wallet
import br.com.webbudget.domain.entities.registration.Wallet.Type.INVESTMENT
import br.com.webbudget.domain.entities.registration.Wallet.Type.PERSONAL
import br.com.webbudget.domain.exceptions.DuplicatedPropertyException
import br.com.webbudget.domain.services.registration.WalletService
import br.com.webbudget.infrastructure.repository.registration.WalletRepository
import br.com.webbudget.utilities.fixture.createWallet
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.test.context.jdbc.Sql
import java.math.BigDecimal
import java.util.UUID

class WalletServiceITest : BaseIntegrationTest() {

    @Autowired
    private lateinit var walletService: WalletService

    @Autowired
    private lateinit var walletRepository: WalletRepository

    @ParameterizedTest
    @MethodSource("costCentersToCreate")
    @Sql("/sql/registration/clear-tables.sql", "/sql/registration/create-wallets.sql")
    fun `should save`(toCreate: Wallet) {

        val externalId = walletService.create(toCreate)

        val created = walletRepository.findByExternalId(externalId)
            ?: fail(OBJECT_NOT_FOUND_ERROR)

        assertThat(created)
            .satisfies({
                assertThat(it.id).isNotNull()
                assertThat(it.externalId).isEqualTo(externalId)
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

        val toCreate = createWallet()

        assertThatThrownBy { walletService.create(toCreate) }
            .isInstanceOf(DuplicatedPropertyException::class.java)
    }

    @Test
    @Sql("/sql/registration/clear-tables.sql", "/sql/registration/create-wallets.sql")
    fun `should not save when banking information is duplicated`() {

        val toCreate = createWallet(name = "Bank account")

        assertThatThrownBy { walletService.create(toCreate) }
            .isInstanceOf(DuplicatedPropertyException::class.java)
    }

    @ParameterizedTest
    @MethodSource("costCentersToUpdate")
    @Sql("/sql/registration/clear-tables.sql", "/sql/registration/create-wallets.sql")
    fun `should update`(externalId: UUID) {

        val toUpdate = walletRepository.findByExternalId(externalId) ?: fail(OBJECT_NOT_FOUND_ERROR)

        toUpdate.apply {
            this.name = "Updated"
            this.description = "Updated"
            this.active = false
            this.bank = "111"
            this.agency = "222"
            this.number = "333"
        }

        val updated = walletService.update(toUpdate)

        assertThat(updated)
            .isNotNull
            .satisfies({
                assertThat(it.id).isEqualTo(toUpdate.id)
                assertThat(it.externalId).isEqualTo(externalId)
                assertThat(it.version).isGreaterThan(toUpdate.version)
                assertThat(it.active).isEqualTo(toUpdate.active)
                assertThat(it.name).isEqualTo(toUpdate.name)
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

        toUpdate.apply { this.name = "Investments" }

        assertThatThrownBy { walletService.update(toUpdate) }
            .isInstanceOf(DuplicatedPropertyException::class.java)
    }

    @Test
    @Sql("/sql/registration/clear-tables.sql", "/sql/registration/create-wallets.sql")
    fun `should not update when banking information is duplicated`() {

        val externalId = UUID.fromString("4ade8a17-460b-40fc-b200-1504bcd4aaf7")

        val toUpdate = walletRepository.findByExternalId(externalId)
            ?: fail(OBJECT_NOT_FOUND_ERROR)

        toUpdate.apply {
            this.bank = "Bank"
            this.agency = "123"
            this.number = "456789"
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
    @Sql(
        "/sql/registration/clear-tables.sql",
        "/sql/registration/create-wallets.sql",
        "/sql/registration/create-cards.sql"
    )
    fun `should fail to delete when in use`() {

        val externalId = UUID.fromString("cd00845c-ae27-47e4-8282-c8df1c42acfe")

        val toDelete = walletRepository.findByExternalId(externalId) ?: fail(OBJECT_NOT_FOUND_ERROR)

        assertThatThrownBy { walletService.delete(toDelete) }
            .isInstanceOf(DataIntegrityViolationException::class.java)
    }

    companion object {

        @JvmStatic
        fun costCentersToUpdate() = listOf(
            Arguments.of(UUID.fromString("d6421251-7b38-4765-88e0-4d70bc3bc4c7")),
            Arguments.of(UUID.fromString("4ade8a17-460b-40fc-b200-1504bcd4aaf7")),
            Arguments.of(UUID.fromString("cd00845c-ae27-47e4-8282-c8df1c42acfe"))
        )

        @JvmStatic
        fun costCentersToCreate() = listOf(
            Arguments.of(
                createWallet(
                    name = "Other personal",
                    type = PERSONAL,
                    bank = null,
                    agency = null,
                    number = null
                )
            ),
            Arguments.of(
                createWallet(
                    name = "Other investments",
                    type = INVESTMENT,
                    balance = BigDecimal.ONE,
                    bank = "Broker",
                    agency = "1",
                    number = "1"
                )
            ),
            Arguments.of(
                createWallet(
                    name = "Other bank account",
                    balance = BigDecimal.TEN,
                    agency = "2",
                    number = "2"
                )
            )
        )
    }
}

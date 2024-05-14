package br.com.webbudget.services.registration

import br.com.webbudget.BaseIntegrationTest
import br.com.webbudget.domain.entities.registration.Card
import br.com.webbudget.domain.exceptions.DuplicatedPropertyException
import br.com.webbudget.domain.services.registration.CardService
import br.com.webbudget.infrastructure.repository.registration.CardRepository
import br.com.webbudget.infrastructure.repository.registration.WalletRepository
import br.com.webbudget.utilities.fixture.createCard
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import java.util.UUID

class CardServiceTest : BaseIntegrationTest() {

    @Autowired
    private lateinit var cardRepository: CardRepository

    @Autowired
    private lateinit var walletRepository: WalletRepository

    @Autowired
    private lateinit var cardService: CardService

    @Test
    @Sql("/sql/registration/clear-tables.sql")
    fun `should create credit card`() {

        val toCreate = createCard(type = Card.Type.CREDIT, wallet = null)
        val externalId = cardService.create(toCreate)

        val created = cardRepository.findByExternalId(externalId)
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
                assertThat(it.lastFourDigits).isEqualTo(toCreate.lastFourDigits)
                assertThat(it.invoicePaymentDay).isEqualTo(toCreate.invoicePaymentDay)
                assertThat(it.flag).isEqualTo(toCreate.flag)
                assertThat(it.wallet).isNull()
            })
    }

    @Test
    @Sql("/sql/registration/clear-tables.sql", "/sql/registration/create-wallets.sql")
    fun `should create debit card`() {

        val wallet = walletRepository.findByExternalId(UUID.fromString("cd00845c-ae27-47e4-8282-c8df1c42acfe"))
            ?: fail(OBJECT_NOT_FOUND_ERROR)

        val toCreate = createCard(type = Card.Type.DEBIT, wallet = wallet)
        val externalId = cardService.create(toCreate)

        val created = cardRepository.findByExternalId(externalId)
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
                assertThat(it.lastFourDigits).isEqualTo(toCreate.lastFourDigits)
                assertThat(it.invoicePaymentDay).isEqualTo(toCreate.invoicePaymentDay)
                assertThat(it.flag).isEqualTo(toCreate.flag)
                assertThat(it.wallet)
                    .isNotNull
                    .satisfies({ wallet ->
                        assertThat(wallet!!.externalId).isEqualTo(wallet.externalId)
                    })
            })
    }

    @Test
    @Sql("/sql/registration/clear-tables.sql")
    fun `should fail to create debit card has no wallet`() {

        val toCreate = createCard(type = Card.Type.DEBIT, wallet = null)

        assertThatThrownBy { cardService.create(toCreate) }
            .isInstanceOf(IllegalStateException::class.java)
    }

    @Test
    @Sql(
        "/sql/registration/clear-tables.sql",
        "/sql/registration/create-wallets.sql",
        "/sql/registration/create-cards.sql"
    )
    fun `should fail to create when type and last four digits is duplicated`() {

        val toCreate = createCard(type = Card.Type.CREDIT, lastFourDigits = "1234")

        assertThatThrownBy { cardService.create(toCreate) }
            .isInstanceOf(DuplicatedPropertyException::class.java)
    }

    @Test
    @Sql(
        "/sql/registration/clear-tables.sql",
        "/sql/registration/create-wallets.sql",
        "/sql/registration/create-cards.sql"
    )
    fun `should update credit card`() {

    }

    @Test
    @Sql(
        "/sql/registration/clear-tables.sql",
        "/sql/registration/create-wallets.sql",
        "/sql/registration/create-cards.sql"
    )
    fun `should update debit card`() {

    }

    @Test
    @Sql("/sql/registration/clear-tables.sql")
    fun `should not update when name is duplicated`() {

    }

    @Test
    @Sql("/sql/registration/clear-tables.sql")
    fun `should delete`() {

        val externalId = cardService.create(createCard(type = Card.Type.CREDIT, wallet = null))

        val created = cardRepository.findByExternalId(externalId)
            ?: fail(OBJECT_NOT_FOUND_ERROR)

        cardService.delete(created)

        val deleted = cardRepository.findByExternalId(externalId)
        assertThat(deleted).isNull()
    }

    @Test
    @Disabled
    fun `should fail to delete when in use`() {
        TODO("Not yet implemented")
    }
}
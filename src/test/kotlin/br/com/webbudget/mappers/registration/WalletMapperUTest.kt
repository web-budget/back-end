package br.com.webbudget.mappers.registration

import br.com.webbudget.application.mappers.registration.WalletMapperImpl
import br.com.webbudget.application.payloads.registration.WalletCreateForm
import br.com.webbudget.application.payloads.registration.WalletUpdateForm
import br.com.webbudget.domain.entities.registration.Wallet
import br.com.webbudget.domain.entities.registration.Wallet.Type.BANK_ACCOUNT
import br.com.webbudget.domain.entities.registration.Wallet.Type.INVESTMENT
import br.com.webbudget.domain.entities.registration.Wallet.Type.PERSONAL
import br.com.webbudget.utilities.fixture.createWallet
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.math.BigDecimal
import java.util.UUID

class WalletMapperUTest {

    private val walletMapper = WalletMapperImpl()

    @ParameterizedTest
    @MethodSource("buildCreateFormParams")
    fun `should map create form to domain object`(form: WalletCreateForm) {

        val domainObject = walletMapper.map(form)

        assertThat(domainObject)
            .isNotNull
            .satisfies({
                assertThat(it.name).isEqualTo(form.name)
                assertThat(it.type).isEqualTo(form.type)
                assertThat(it.description).isEqualTo(form.description)
                assertThat(it.bank).isEqualTo(form.bank)
                assertThat(it.number).isEqualTo(form.number)
                assertThat(it.agency).isEqualTo(form.agency)
                assertThat(it.active).isTrue()
                assertThat(it.currentBalance).isEqualTo(BigDecimal.ZERO)
            })
    }

    @Test
    fun `should map create form to domain object`() {

        val domainObject = createWallet()
        val form = WalletUpdateForm("Wallet X", false, "Some wallet", "1", "2", "3")

        walletMapper.map(form, domainObject)

        assertThat(domainObject)
            .isNotNull
            .satisfies({
                assertThat(it.name).isEqualTo(form.name)
                assertThat(it.description).isEqualTo(form.description)
                assertThat(it.active).isEqualTo(form.active)
                assertThat(it.bank).isEqualTo(form.bank)
                assertThat(it.agency).isEqualTo(form.agency)
                assertThat(it.number).isEqualTo(form.number)
            })
    }

    @ParameterizedTest
    @MethodSource("buildWalletParams")
    fun `should map domain object to view`(domainObject: Wallet) {

        val externalId = UUID.randomUUID()

        domainObject.apply {
            this.id = 1L
            this.externalId = externalId
        }

        val view = walletMapper.map(domainObject)

        assertThat(view)
            .isNotNull
            .satisfies({
                assertThat(it.id).isEqualTo(domainObject.externalId!!)
                assertThat(it.active).isEqualTo(domainObject.active)
                assertThat(it.name).isEqualTo(domainObject.name)
                assertThat(it.description).isEqualTo(domainObject.description)
                assertThat(it.type).isEqualTo(domainObject.type)
                assertThat(it.currentBalance).isEqualTo(domainObject.currentBalance)
                assertThat(it.bank).isEqualTo(domainObject.bank)
                assertThat(it.agency).isEqualTo(domainObject.agency)
                assertThat(it.number).isEqualTo(domainObject.number)
            })
    }

    companion object {

        @JvmStatic
        fun buildCreateFormParams() = listOf(
            Arguments.of(WalletCreateForm("Personal", PERSONAL, "Personal")),
            Arguments.of(WalletCreateForm("Investments", INVESTMENT, "Investments", "1", "1", "1")),
            Arguments.of(WalletCreateForm("Bank", BANK_ACCOUNT, "Bank account", "1", "1", "1"))
        )

        @JvmStatic
        fun buildWalletParams() = listOf(
            Arguments.of(createWallet(name = "Personal", type = PERSONAL, description = "Personal")),
            Arguments.of(createWallet(name = "Bank", balance = BigDecimal.TEN)),
            Arguments.of(
                createWallet(
                    name = "Investments",
                    type = INVESTMENT,
                    balance = BigDecimal.ONE,
                    active = false,
                    description = "Investments"
                )
            )
        )
    }
}

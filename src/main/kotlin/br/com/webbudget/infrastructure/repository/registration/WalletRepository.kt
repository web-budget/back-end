package br.com.webbudget.infrastructure.repository.registration

import br.com.webbudget.domain.entities.registration.Wallet
import br.com.webbudget.infrastructure.repository.DefaultRepository
import br.com.webbudget.infrastructure.repository.SpecificationHelpers
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface WalletRepository : DefaultRepository<Wallet> {

    fun findByNameIgnoreCase(description: String): Wallet?

    fun findByNameIgnoreCaseAndExternalIdNot(description: String, externalId: UUID): Wallet?

    @Query(
        "from Wallet w " +
                "where w.bank = :bank " +
                "and w.agency = :agency " +
                "and w.number = :number "
    )
    fun findByBankInfo(bank: String?, agency: String?, number: String?): Wallet?

    @Query(
        "from Wallet w " +
                "where w.bank = :bank " +
                "and w.agency = :agency " +
                "and w.number = :number " +
                "and w.externalId <> :externalId"
    )
    fun findByBankInfo(bank: String?, agency: String?, number: String?, externalId: UUID): Wallet?

    object Specifications : SpecificationHelpers {

        fun byName(name: String?) = Specification<Wallet> { root, _, builder ->
            name?.let { builder.like(builder.lower(root.get("name")), likeIgnoringCase(name)) }
        }

        fun byDescription(description: String?) = Specification<Wallet> { root, _, builder ->
            description?.let { builder.like(builder.lower(root.get("description")), likeIgnoringCase(description)) }
        }

        fun byBankName(bankName: String?) = Specification<Wallet> { root, _, builder ->
            bankName?.let { builder.like(builder.lower(root.get("bank")), likeIgnoringCase(bankName)) }
        }

        fun byAgency(agency: String?) = Specification<Wallet> { root, _, builder ->
            agency?.let { builder.equal(root.get<String>("agency"), agency) }
        }

        fun byNumber(number: String?) = Specification<Wallet> { root, _, builder ->
            number?.let { builder.equal(root.get<String>("number"), number) }
        }

        fun byActive(active: Boolean?) = Specification<Wallet> { root, _, builder ->
            active?.let { builder.equal(root.get<Boolean>("active"), active) }
        }
    }
}

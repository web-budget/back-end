package br.com.webbudget.domain.entities

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.PrePersist
import jakarta.persistence.Version
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.io.Serializable
import java.time.LocalDateTime
import java.util.UUID

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
open class PersistentEntity<T : Serializable> {

    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    @field:Column(name = "id", updatable = false, unique = true, nullable = false)
    var id: T? = null

    @field:Version
    @field:Column(name = "version", nullable = false)
    var version: Short? = null

    @field:Column(name = "external_id", length = 36, updatable = false, unique = true, nullable = false)
    var externalId: UUID? = null

    @field:CreatedDate
    @field:Column(name = "created_on", nullable = false)
    lateinit var createdOn: LocalDateTime

    @field:LastModifiedDate
    @field:Column(name = "last_update", nullable = false)
    lateinit var lastUpdate: LocalDateTime

    @PrePersist
    @Suppress("UnusedPrivateMember")
    private fun onPersist() {
        if (externalId == null) {
            externalId = UUID.randomUUID()
        }
    }

    fun isSaved(): Boolean {
        return this.id != null && this.id != 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PersistentEntity<*>) return false
        if (id != other.id) return false
        return externalId == other.externalId!!
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (externalId.hashCode())
        return result
    }
}

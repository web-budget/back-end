package br.com.webbudget.domain.entities

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.io.Serializable
import java.time.LocalDateTime
import java.util.UUID
import javax.persistence.Column
import javax.persistence.EntityListeners
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.MappedSuperclass
import javax.persistence.PrePersist
import javax.persistence.PreUpdate
import javax.persistence.Version

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
open class PersistentEntity<T : Serializable> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, unique = true)
    var id: T? = null
        private set

    @Column(name = "external_id", length = 36, updatable = false, unique = true)
    var externalId: UUID? = null
        private set

    @CreatedDate
    @Column(name = "created_on", nullable = false)
    var createdOn: LocalDateTime? = null
        private set

    @Column(name = "last_update", nullable = false)
    var lastUpdate: LocalDateTime? = null
        private set

    @Version
    var version: Short? = null
        private set

    fun isSaved(): Boolean {
        return this.id != null && this.id != 0 && this.externalId != null
    }

    @PrePersist
    private fun onPersist() {
        if (externalId == null) {
            externalId = UUID.randomUUID()
        }
    }

    @PreUpdate
    private fun onUpdate() {
        this.lastUpdate = LocalDateTime.now()
    }
}

package br.com.webbudget.backend.domain.entities

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.io.Serializable
import java.time.LocalDateTime
import java.util.UUID
import javax.persistence.Column
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.MappedSuperclass
import javax.persistence.PrePersist
import javax.persistence.Version

@MappedSuperclass
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

    @LastModifiedDate
    @Column(name = "created_on", nullable = false)
    var lastUpdate: LocalDateTime? = null
        private set

    @Version
    var version: Short? = null
        private set

    @PrePersist
    fun onPersist() {
        if (this.externalId == null) {
            this.externalId = UUID.randomUUID()
        }
    }
}
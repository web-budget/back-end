package br.com.webbudget.backend.infrastructure.repository

import br.com.webbudget.backend.domain.entities.PersistentEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.NoRepositoryBean
import java.util.Optional
import java.util.UUID

@NoRepositoryBean
interface DefaultRepository<T : PersistentEntity<Long>> : JpaRepository<T, Long> {

    fun findByExternalId(uuid: UUID): Optional<T>
}
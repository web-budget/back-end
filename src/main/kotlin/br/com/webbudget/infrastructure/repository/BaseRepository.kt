package br.com.webbudget.infrastructure.repository

import br.com.webbudget.domain.entities.PersistentEntity
import io.hypersistence.utils.spring.repository.BaseJpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.NoRepositoryBean
import java.util.UUID

@NoRepositoryBean
interface BaseRepository<T : PersistentEntity<Long>> : BaseJpaRepository<T, Long>, JpaSpecificationExecutor<T> {

    fun findByExternalId(uuid: UUID): T?
}

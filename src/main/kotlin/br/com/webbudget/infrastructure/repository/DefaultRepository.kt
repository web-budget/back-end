package br.com.webbudget.infrastructure.repository

import br.com.webbudget.domain.entities.PersistentEntity
import io.hypersistence.utils.spring.repository.HibernateRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.NoRepositoryBean
import java.util.UUID

@NoRepositoryBean
interface DefaultRepository<T : PersistentEntity<Long>> : HibernateRepository<T>, JpaRepository<T, Long>,
    JpaSpecificationExecutor<T> {

    fun findByExternalId(uuid: UUID): T?

    fun deleteByExternalId(uuid: UUID)
}

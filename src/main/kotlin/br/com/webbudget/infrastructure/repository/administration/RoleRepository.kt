package br.com.webbudget.infrastructure.repository.administration

import br.com.webbudget.domain.entities.administration.Role
import br.com.webbudget.infrastructure.repository.BaseRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface RoleRepository : BaseRepository<Role> {

    fun findByName(name: String): Role?

    @Query("from Role as r order by r.name")
    fun findAll(): List<Role>
}

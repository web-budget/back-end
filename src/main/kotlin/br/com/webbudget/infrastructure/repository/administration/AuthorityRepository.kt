package br.com.webbudget.infrastructure.repository.administration

import br.com.webbudget.domain.entities.administration.Authority
import br.com.webbudget.infrastructure.repository.BaseRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface AuthorityRepository : BaseRepository<Authority> {

    fun findByName(name: String): Authority?

    @Query("from Authority")
    fun findAll(): List<Authority>
}

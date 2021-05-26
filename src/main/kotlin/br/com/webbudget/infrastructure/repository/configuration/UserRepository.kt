package br.com.webbudget.infrastructure.repository.configuration

import br.com.webbudget.application.payloads.UserFilter
import br.com.webbudget.domain.entities.configuration.User
import br.com.webbudget.infrastructure.repository.DefaultRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : DefaultRepository<User> {

    fun findByEmail(username: String?): User?

    @Query(
        "from User u " +
            "where (:#{#filter.name} is null or u.name like :#{#filter.name}) " +
            "and (:#{#filter.email} is null or u.email like :#{#filter.email}) " +
            "and (:#{#filter.active} is null or u.active = :#{#filter.active}) "
    )
    fun findByFilter(@Param("filter") filter: UserFilter, pageable: Pageable): Page<User>
}

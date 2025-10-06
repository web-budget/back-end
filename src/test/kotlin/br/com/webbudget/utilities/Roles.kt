package br.com.webbudget.utilities

import br.com.webbudget.domain.entities.administration.Role

object Roles {

    const val ADMINISTRATION = "ADMINISTRATION"
    const val FINANCIAL = "FINANCIAL"
    const val REGISTRATION = "REGISTRATION"
    const val DASHBOARDS = "DASHBOARDS"

    fun asList(): List<Role> {
        return listOf(
            Role(ADMINISTRATION),
            Role(REGISTRATION),
            Role(FINANCIAL),
            Role(DASHBOARDS)
        )
    }
}

package br.com.webbudget.domain.mail

import br.com.webbudget.domain.entities.administration.Language.EN_US
import br.com.webbudget.domain.entities.administration.User

class AccountActivationEmail(user: User) : MailTemplate(
    "account-activation-${user.defaultLanguage.text}",
    if (user.defaultLanguage == EN_US) SUBJECT_EN else SUBJECT_PT,
    listOf(user.email)
) {

    init {
        this.populateVariables(user)
    }

    private fun populateVariables(user: User) {
        super.addVariable("user", user.name)
    }

    companion object {
        private const val SUBJECT_EN = "User account activation"
        private const val SUBJECT_PT = "Ativar conta de usuário"
    }
}

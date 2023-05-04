package br.com.webbudget.domain.mail

import br.com.webbudget.domain.entities.administration.Language.EN_US
import br.com.webbudget.domain.entities.administration.User

class RecoverPasswordEmail(user: User) : MailTemplate(
    "recover-password-${user.defaultLanguage.text}",
    if (user.defaultLanguage == EN_US) SUBJECT_EN else SUBJECT_PT,
    listOf(user.email)
) {

    init {
        this.populateVariables(user)
    }

    private fun populateVariables(user: User) {
        super.addVariable("user", user.name)
        super.addVariable("email", user.email)
    }

    companion object {
        private const val SUBJECT_EN = "Password recovery"
        private const val SUBJECT_PT = "Recuperação de senha"
    }
}

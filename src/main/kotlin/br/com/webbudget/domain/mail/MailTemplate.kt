package br.com.webbudget.domain.mail

import jakarta.mail.internet.InternetAddress

open class MailTemplate(
    val name: String,
    val subject: String,
    val to: List<String>,
    val variables: MutableMap<String, Any> = mutableMapOf()
) {

    fun addVariable(key: String, value: Any) {
        this.variables[key] = value
    }

    fun toAsInternetAddress() = to.map { InternetAddress(it) }.toTypedArray()
}

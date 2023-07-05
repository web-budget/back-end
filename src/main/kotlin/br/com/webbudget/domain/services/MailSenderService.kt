package br.com.webbudget.domain.services

import br.com.webbudget.domain.mail.MailTemplate
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.mail.internet.InternetAddress
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.thymeleaf.context.Context
import org.thymeleaf.spring6.SpringTemplateEngine
import java.nio.charset.StandardCharsets.UTF_8

private val logger = KotlinLogging.logger {}

@Service
class MailSenderService(
    private val mailSender: JavaMailSender,
    private val templateEngine: SpringTemplateEngine,
    @Value("\${web-budget.mail.default-from-address}")
    private val defaultFromAddress: String,
    @Value("\${web-budget.mail.reply-to-address}")
    private val replyToAddress: String,
    @Value("\${web-budget.mail.logo-url}")
    private val logoUrl: String,
    @Value("\${web-budget.front-end-url}")
    private val frontendUrl: String
) {

    @Async
    fun sendEmail(mailTemplate: MailTemplate) {

        logger.debug { "Trying to send e-mail [${mailTemplate.subject}] to ${mailTemplate.to}" }

        val context = Context()

        context.setVariables(mailTemplate.variables)

        context.setVariable("logoUrl", logoUrl)
        context.setVariable("applicationUrl", frontendUrl)

        val template = templateEngine.process(mailTemplate.name, context)

        val mimeMessage = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(mimeMessage, true, UTF_8.name())

        helper.setSubject(mailTemplate.subject)
        helper.setTo(mailTemplate.toAsInternetAddress())
        helper.setFrom(InternetAddress(defaultFromAddress, "webBudget"))
        helper.setReplyTo(replyToAddress)
        helper.setText(template, true)

        mailSender.send(mimeMessage)

        logger.debug { "E-mail [${mailTemplate.subject}] to ${mailTemplate.to} sent" }
    }
}

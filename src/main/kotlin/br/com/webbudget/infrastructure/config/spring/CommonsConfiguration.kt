package br.com.webbudget.infrastructure.config.spring

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.ApplicationEventMulticaster
import org.springframework.context.event.SimpleApplicationEventMulticaster
import org.springframework.core.task.support.TaskExecutorAdapter
import org.springframework.scheduling.annotation.EnableScheduling
import org.thymeleaf.spring6.SpringTemplateEngine
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver
import org.thymeleaf.templatemode.TemplateMode.HTML
import java.nio.charset.StandardCharsets
import java.util.concurrent.Executors

@Configuration
@EnableScheduling
class CommonsConfiguration {

    private lateinit var templateResolver: SpringResourceTemplateResolver

    @Bean
    fun configureTemplateEngine(): SpringTemplateEngine {

        val templateResolver = configureTemplateResolver()

        val engine = SpringTemplateEngine()
        engine.addTemplateResolver(templateResolver)

        return engine
    }

    @Bean
    fun configureTemplateResolver(): SpringResourceTemplateResolver {

        if (::templateResolver.isInitialized) {
            return templateResolver
        }

        templateResolver = SpringResourceTemplateResolver()

        templateResolver.prefix = "classpath:/mail-templates/"
        templateResolver.suffix = ".html"
        templateResolver.templateMode = HTML
        templateResolver.characterEncoding = StandardCharsets.UTF_8.name()

        return templateResolver
    }

    @Bean
    fun configureEventMulticaster(): ApplicationEventMulticaster {

        val virtualThreadsTaskExecutor = TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor())

        val eventMulticaster = SimpleApplicationEventMulticaster()
        eventMulticaster.setTaskExecutor(virtualThreadsTaskExecutor)

        return eventMulticaster
    }
}

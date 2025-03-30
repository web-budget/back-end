package br.com.webbudget.infrastructure.config.spring

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.context.event.ApplicationEventMulticaster
import org.springframework.context.event.SimpleApplicationEventMulticaster
import org.springframework.core.task.support.TaskExecutorAdapter
import org.springframework.scheduling.annotation.EnableAsync
import java.util.concurrent.Executors

@EnableAsync
@Profile("!test")
@Configuration(proxyBeanMethods = false)
class AsyncConfiguration {

    @Bean
    fun configureEventMulticaster(): ApplicationEventMulticaster {

        val virtualThreadsTaskExecutor = TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor())

        val eventMulticaster = SimpleApplicationEventMulticaster()
        eventMulticaster.setTaskExecutor(virtualThreadsTaskExecutor)

        return eventMulticaster
    }
}
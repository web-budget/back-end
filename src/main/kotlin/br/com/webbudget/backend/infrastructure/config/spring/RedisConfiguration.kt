package br.com.webbudget.backend.infrastructure.config.spring

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory


@Configuration
class RedisConfiguration(
    @Value("\${redis.host:localhost}")
    private val hostname: String,
    @Value("\${redis.host:6379}")
    private val port: Int
) {

    @Bean
    fun lettuceConnectionFactory(): LettuceConnectionFactory {
        return LettuceConnectionFactory(RedisStandaloneConfiguration(this.hostname, this.port))
    }
}
package br.com.webbudget.backend.domain.services

import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class TokenCacheService(
    private val redisTemplate: StringRedisTemplate,
    @Value("\${web-budget.jwt.seconds-to-expire}")
    private val secondsToExpire: Long
) {

    fun store(subject: String, token: String) {
        this.redisTemplate.opsForHash<String, String>().put(COLLECTION, subject, token)
        this.redisTemplate.expire(subject, secondsToExpire, TimeUnit.SECONDS)
    }

    fun find(subject: String): String? {
        return this.redisTemplate.opsForHash<String, String>().get(COLLECTION, subject)
    }

    fun remove(subject: String) {
        this.redisTemplate.opsForHash<String, String>().delete(COLLECTION, subject)
    }

    fun isExpired(subject: String): Boolean {
        return this.find(subject).isNullOrBlank()
    }

    companion object {
        private const val COLLECTION = "tokens"
    }
}
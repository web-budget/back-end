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
        redisTemplate.opsForHash<String, String>().put(COLLECTION, subject, token)
        redisTemplate.expire(subject, secondsToExpire, TimeUnit.SECONDS)
    }

    fun find(subject: String): String? {
        return redisTemplate.opsForHash<String, String>().get(COLLECTION, subject)
    }

    fun remove(subject: String) {
        redisTemplate.opsForHash<String, String>().delete(COLLECTION, subject)
    }

    fun isExpired(subject: String): Boolean {
        return find(subject).isNullOrBlank()
    }

    companion object {
        private const val COLLECTION = "tokens"
    }
}
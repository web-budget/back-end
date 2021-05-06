package br.com.webbudget.backend.domain.services

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class CacheService(
    private val redisTemplate: RedisTemplate<String, Any>
) {

    fun store(key: String, value: Any, expiration: Long = 7200) {
        redisTemplate.opsForValue().set(key, value)
        redisTemplate.expire(key, expiration, TimeUnit.SECONDS)
    }

    fun find(key: String): Any? {
        return redisTemplate.opsForValue().get(key)
    }

    fun remove(key: String) {
        redisTemplate.delete(key)
    }

    fun isExpired(key: String): Boolean {
        return find(key).let { false }
    }
}
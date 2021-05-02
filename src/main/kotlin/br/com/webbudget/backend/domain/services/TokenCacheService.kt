package br.com.webbudget.backend.domain.services

import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.util.UUID
import java.util.concurrent.TimeUnit

@Service
class TokenCacheService(
    private val redisTemplate: StringRedisTemplate,
    @Value("\${web-budget.jwt.access-token-expiration}")
    private val accessTokenExpiration: Long,
    @Value("\${web-budget.jwt.refresh-token-expiration}")
    private val refreshTokenExpiration: Long,
) {

    fun storeAccessToken(subject: String, token: String) {
        redisTemplate.opsForValue().set(accessTokenKey(subject), token)
        redisTemplate.expire(accessTokenKey(subject), accessTokenExpiration, TimeUnit.SECONDS)
    }

    fun storeRefreshToken(subject: String, refreshToken: UUID) {
        redisTemplate.opsForValue().set(refreshTokenKey(subject), refreshToken.toString())
        redisTemplate.expire(refreshTokenKey(subject), refreshTokenExpiration, TimeUnit.SECONDS)
    }

    fun findAccessToken(subject: String): String? {
        return redisTemplate.opsForValue().get(accessTokenKey(subject))
    }

    fun findRefreshToken(subject: String): UUID? {
        return redisTemplate.opsForValue().get(refreshTokenKey(subject))?.let { UUID.fromString(it) }
    }

    fun remove(subject: String) {
        redisTemplate.delete(accessTokenKey(subject))
        redisTemplate.delete(refreshTokenKey(subject))
    }

    fun isExpired(subject: String): Boolean {
        return findAccessToken(subject).isNullOrBlank()
    }

    private fun accessTokenKey(subject: String): String = accessTokenKey + subject

    private fun refreshTokenKey(subject: String): String = refreshTokenKey + subject

    companion object {
        private const val accessTokenKey = "access_token:"
        private const val refreshTokenKey = "refresh_token:"
    }
}
package br.com.webbudget.infrastructure.config.spring

import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

object KeyPairGenerator {

    private val keyPair: KeyPair

    init {
        keyPair = generatePairs()
    }

    fun getPrivateKey(): RSAPrivateKey {
        return keyPair.private as RSAPrivateKey
    }

    fun getPublicKey(): RSAPublicKey {
        return keyPair.public as RSAPublicKey
    }

    private fun generatePairs(): KeyPair {
        val generator = KeyPairGenerator.getInstance(KEY_ALGORITHM)
        generator.initialize(KEY_SIZE)
        return generator.generateKeyPair()
    }

    private const val KEY_SIZE = 4096
    private const val KEY_ALGORITHM = "RSA"
}

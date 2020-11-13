package com.rwawrzyniak.securephotos.ui.main.encryption

import java.security.SecureRandom
import java.security.spec.KeySpec
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject

class SecretKeyGenerator @Inject constructor()  {

	private val random by lazy { SecureRandom() }

	fun createSecretKey(password: String): SecretKey {
		val salt = ByteArray(SALT_SIZE)
		random.nextBytes(salt)

		val factory: SecretKeyFactory = SecretKeyFactory.getInstance(SECRET_KEY_ALGORITHM)
		val spec: KeySpec = PBEKeySpec(password.toCharArray(), salt, iterationCount, keyStrength)
		val tmp: SecretKey = factory.generateSecret(spec)
		return SecretKeySpec(tmp.encoded, "AES")
	}

	companion object{
		private const val SECRET_KEY_ALGORITHM = "PBKDF2WithHmacSHA1"
		private const val SALT_SIZE = 32
		private const val iterationCount = 1024
		private const val keyStrength = 256
	}
}

package com.rwawrzyniak.securephotos.encryption

import com.rwawrzyniak.securephotos.encryption.CryptoParameters.ENCRYPTION_ALGORITHM_AES
import com.rwawrzyniak.securephotos.encryption.CryptoParameters.SALT_SIZE
import com.rwawrzyniak.securephotos.encryption.CryptoParameters.SECRET_KEY_ALGORITHM
import com.rwawrzyniak.securephotos.encryption.CryptoParameters.iterationCount
import com.rwawrzyniak.securephotos.encryption.CryptoParameters.keyStrength
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
		return SecretKeySpec(tmp.encoded, ENCRYPTION_ALGORITHM_AES)
	}
}

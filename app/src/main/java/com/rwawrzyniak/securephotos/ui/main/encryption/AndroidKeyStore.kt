package com.rwawrzyniak.securephotos.ui.main.encryption

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.VisibleForTesting
import java.security.Key
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.inject.Inject

internal class AndroidKeyStore @Inject @VisibleForTesting internal constructor() {

	private val keyStore: KeyStore by lazy { loadKeyStore(KEY_STORE_NAME) }

	fun createKey(alias: String): SecretKey {

		val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEY_STORE_NAME)
		val builder = KeyGenParameterSpec.Builder(alias,
			KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
			.setKeySize(AES_KEY_SIZE)
			.setBlockModes(KeyProperties.BLOCK_MODE_CBC)
//			.setUserAuthenticationRequired(userAuthenticationRequired)
			// we create our own random IV
			.setRandomizedEncryptionRequired(false)
			.setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			builder.setInvalidatedByBiometricEnrollment(true)
		}

		keyGenerator.init(builder.build())
		return keyGenerator.generateKey()
	}

	fun retrieveKey(alias: String, password: CharArray? = null): Key =
		keyStore.getKey(alias, password) ?: throw IllegalArgumentException("No key with alias $alias")

	fun keyExists(alias: String): Boolean = keyStore.aliases().asSequence().any { it == alias }

	private fun loadKeyStore(keyStoreName: String): KeyStore =
		KeyStore.getInstance(keyStoreName).also {
			it.load(null)
		}

	companion object {
		internal const val AES_KEY_SIZE = 256
		internal const val KEY_STORE_NAME = "AndroidKeyStore"
	}
}

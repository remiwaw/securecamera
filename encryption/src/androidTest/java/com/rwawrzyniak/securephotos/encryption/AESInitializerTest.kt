package com.rwawrzyniak.securephotos.encryption

import android.util.Base64
import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.Before
import org.junit.Test


class AESInitializerTest {
	private lateinit var aesInitializer: AESInitializer
	private lateinit var secretKeyGenerator: SecretKeyGenerator
	private val testPassword = "test123"

	@Before
	fun setUp() {
		aesInitializer = AESInitializer()
		secretKeyGenerator = SecretKeyGenerator()
	}

	@Test
	fun shouldInitializeCipherForEncryptionWithRandomIV() {
		val cipher = aesInitializer.initialize(Mode.ENCRYPT, secretKeyGenerator.createSecretKey(testPassword))
		assertThat(cipher.iv.size).isEqualTo(AES_BLOCK_SIZE)
	}

	@Test
	fun shouldInitializeCipherForEncryptionWithFixedIV() {
		val secretKey = secretKeyGenerator.createSecretKey(testPassword)
		val cipher = aesInitializer.initialize(Mode.ENCRYPT, secretKey, IV)

		assertThat(cipher.iv).isEqualTo(IV)
	}

	@Test(expected = IllegalArgumentException::class)
	fun shouldFailToInitializeCipherForDecryptionWithNoIV() {
		val secretKey = secretKeyGenerator.createSecretKey(testPassword)
		aesInitializer.initialize(Mode.DECRYPT, secretKey)
	}

	companion object {
		private const val AES_BLOCK_SIZE = 16
		private val IV = Base64.decode("R0/MbCVQItRugf02KscEMA==", Base64.DEFAULT)
	}
}


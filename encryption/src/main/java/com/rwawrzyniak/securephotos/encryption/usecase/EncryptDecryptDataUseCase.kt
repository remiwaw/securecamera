package com.rwawrzyniak.securephotos.encryption.usecase

import androidx.annotation.VisibleForTesting
import com.rwawrzyniak.securephotos.core.android.ext.toByteArray
import com.rwawrzyniak.securephotos.encryption.AESInitializer
import com.rwawrzyniak.securephotos.encryption.CryptoParameters.IV_SIZE
import com.rwawrzyniak.securephotos.encryption.Mode
import java.io.File
import javax.inject.Inject

class EncryptDecryptDataUseCase @VisibleForTesting @Inject internal constructor(
	private val findOrCreateKeyUseCase: FindOrCreateKeyUseCase,
	private val aesInitializer: AESInitializer
)  {

	fun encrypt(file: File): ByteArray = encrypt(file.toByteArray())

	fun encrypt(plainByteArray: ByteArray): ByteArray {
		val cipher = aesInitializer.initialize(Mode.ENCRYPT, findOrCreateKey())
		val encrypted = cipher.doFinal(plainByteArray)
		val iv = cipher.iv
		// sanity, because we're later reading that much bytes again
		require(iv.size == IV_SIZE) { "IV is of wrong size!" }
		val encryptedByteArray = iv + encrypted
		return encryptedByteArray
	}

	fun decrypt(file: File): ByteArray {
		val encryptedContents = file.toByteArray()
		val iv = encryptedContents.copyOf(IV_SIZE)
		val payload = encryptedContents.copyOfRange(IV_SIZE, encryptedContents.size)
		val cipher = aesInitializer.initialize(Mode.DECRYPT, findOrCreateKey(), iv)
		return cipher.doFinal(payload)
	}

	private fun findOrCreateKey() =
		findOrCreateKeyUseCase.findOrCreateKey()
}

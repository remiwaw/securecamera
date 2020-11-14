package com.rwawrzyniak.securephotos.ui.main.encryption.usecase

import androidx.annotation.VisibleForTesting
import com.rwawrzyniak.securephotos.ext.toByteArray
import com.rwawrzyniak.securephotos.ui.main.encryption.AESInitializer
import com.rwawrzyniak.securephotos.ui.main.encryption.FindOrCreateKeyUseCase
import com.rwawrzyniak.securephotos.ui.main.encryption.Mode
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
		require(iv.size == AESInitializer.IV_SIZE) { "IV is of wrong size!" }
		val encryptedByteArray = iv + encrypted
		return encryptedByteArray
	}

	fun decrypt(file: File): ByteArray {
		val encryptedContents = file.toByteArray()
		val iv = encryptedContents.copyOf(AESInitializer.IV_SIZE)
		val payload = encryptedContents.copyOfRange(AESInitializer.IV_SIZE, encryptedContents.size)
		val cipher = aesInitializer.initialize(Mode.DECRYPT, findOrCreateKey(), iv)
		return cipher.doFinal(payload)
	}

	private fun findOrCreateKey() =
		findOrCreateKeyUseCase.findOrCreateKey()
}

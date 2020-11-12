package com.rwawrzyniak.securephotos.ui.main

import com.rwawrzyniak.securephotos.ext.toByteArray
import java.io.File
import java.io.FileInputStream
import java.security.Key
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject


class EncryptDecryptDataUseCase @Inject constructor() {

	fun decrypt(name: String, encryptedBarArray: ByteArray): ByteArray {
		return doCryptoInAES(Cipher.DECRYPT_MODE, "ak$#54%^RtF%g^Hf", encryptedBarArray);
	}

	fun encrypt(file: File): ByteArray {
		return doCryptoInAES(Cipher.ENCRYPT_MODE, "ak$#54%^RtF%g^Hf", file.toByteArray());
	}

	@Throws(CryptoException::class)
	private fun doCryptoInAES(
		cipherMode: Int,
		key: String,
		inputBytes: ByteArray
	): ByteArray {
		try {
			val secretKey: Key = SecretKeySpec(key.toByteArray(), "AES")
			val cipher: Cipher = Cipher.getInstance("AES")
			cipher.init(cipherMode, secretKey)
			return cipher.doFinal(inputBytes)
		} catch (ex: Exception) {
			throw CryptoException("Error encrypting/decrypting file", ex)
		}
	}
}


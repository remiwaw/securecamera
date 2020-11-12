package com.rwawrzyniak.securephotos.ui.main.encryption

import androidx.annotation.VisibleForTesting
import java.security.Key
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.inject.Inject

// This class prepare Cipher based on key and initializationVector
class AESInitializer @Inject @VisibleForTesting internal constructor(){

    private val random by lazy { SecureRandom() }

    fun initialize(mode: Mode, key: Key, initializationVector: ByteArray = byteArrayOf()): Cipher {
        require(mode == Mode.ENCRYPT || initializationVector.isNotEmpty()) {
            "missing initialization vector (IV) for decryption"
        }
        val iv = if (initializationVector.isEmpty()) randomIV() else initializationVector
        return createCipher().also {
            it.init(mode.value, key, IvParameterSpec(iv))
        }
    }

    private fun randomIV(): ByteArray = ByteArray(IV_SIZE).also { random.nextBytes(it) }

    private fun createCipher(): Cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")

    companion object {
        // AES block size is 16 byte, so our IV must be of the same size
        const val IV_SIZE = 16
    }
}

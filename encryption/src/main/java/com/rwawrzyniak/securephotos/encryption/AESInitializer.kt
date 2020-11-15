package com.rwawrzyniak.securephotos.encryption

import androidx.annotation.VisibleForTesting
import com.rwawrzyniak.securephotos.encryption.CryptoParameters.CIPHER_TRANSFORMATION
import com.rwawrzyniak.securephotos.encryption.CryptoParameters.IV_SIZE
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

    private fun createCipher(): Cipher = Cipher.getInstance(CIPHER_TRANSFORMATION)
}

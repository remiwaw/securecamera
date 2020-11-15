package com.rwawrzyniak.securephotos.encryption

import javax.crypto.Cipher

// Type-safe encryption mode
enum class Mode(internal val value: Int) { ENCRYPT(Cipher.ENCRYPT_MODE), DECRYPT(Cipher.DECRYPT_MODE) }

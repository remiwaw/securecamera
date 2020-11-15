package com.rwawrzyniak.securephotos.encryption

object CryptoParameters {
	// AES block size is 16 byte, so our IV must be of the same size
	const val IV_SIZE = 16
	const val CIPHER_TRANSFORMATION = "AES/CBC/PKCS7Padding"
	const val ENCRYPTION_ALGORITHM_AES = "AES"
	const val ENCRYPTED_SECRET_SHARED_PREFERENCES_KEY = "encrypted_secret_key"

	// Generating secret key params
	const val SECRET_KEY_ALGORITHM = "PBKDF2WithHmacSHA1"
	const val SALT_SIZE = 32
	const val iterationCount = 1024
	const val keyStrength = 256
}

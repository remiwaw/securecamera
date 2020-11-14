package com.rwawrzyniak.securephotos.ui.main.encryption

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import com.rwawrzyniak.securephotos.Constants.ENCRYPTED_PREFS_APP_CODE_KEY
import com.rwawrzyniak.securephotos.ext.fromBase64
import com.rwawrzyniak.securephotos.ext.toBase64
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

class FindOrCreateKeyUseCase internal constructor(
	private val context: Context,
	private val secretKeyGenerator: SecretKeyGenerator,
	private val sharedPreferences: EncryptedSharedPreferences
) {

    fun findOrCreateKey(): SecretKey =
        if (sharedPreferences.contains(ENCRYPTED_SECRET_KEY)) {
			val base64EncodedSecretKey = requireNotNull(sharedPreferences.getString(
				ENCRYPTED_SECRET_KEY, null
			))
			SecretKeySpec(base64EncodedSecretKey.fromBase64(), "AES")
		} else {
			var plainPassword = requireNotNull(sharedPreferences.getString(ENCRYPTED_PREFS_APP_CODE_KEY, null))
			val key = secretKeyGenerator.createSecretKey(plainPassword)
			sharedPreferences.edit().putString(ENCRYPTED_SECRET_KEY, key.encoded.toBase64()).apply()
			key
        }

	companion object {
		private const val ENCRYPTED_SECRET_KEY = "encrypted_secret_key"
	}
}

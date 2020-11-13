package com.rwawrzyniak.securephotos.ui.main.encryption

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.rwawrzyniak.securephotos.ext.fromBase64
import com.rwawrzyniak.securephotos.ext.toBase64
import java.security.Key
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

class FindOrCreateKeyUseCase @VisibleForTesting internal constructor(
	private val context: Context,
	private val secretKeyGenerator: SecretKeyGenerator
) {

	// https://github.com/android/security-samples/tree/master/FileLocker
	private val sharedPreferences by lazy {
		EncryptedSharedPreferences.create(
			ENCRYPTED_PREFS_FILE_NAME,
			MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
			context,
			EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
			EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
		)
	}

    fun findOrCreateKey(): SecretKey =
        if (sharedPreferences.contains(ENCRYPTED_SECRET_KEY)) {
			val base64EncodedSecretKey = requireNotNull(sharedPreferences.getString(
				ENCRYPTED_SECRET_KEY, null
			))
			SecretKeySpec(base64EncodedSecretKey.fromBase64(), "AES")
		} else {
			val key = secretKeyGenerator.createSecretKey(PLAIN_TEXT_PASSWORD)
			sharedPreferences.edit().putString(ENCRYPTED_SECRET_KEY, key.encoded.toBase64()).apply()
			key
        }

	companion object {
		private const val PLAIN_TEXT_PASSWORD = "YND123!"
		private const val ENCRYPTED_PREFS_FILE_NAME = "safe_preferences"
		private const val ENCRYPTED_SECRET_KEY = "encrypted_secret_key"
	}
}

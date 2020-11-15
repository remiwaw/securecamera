package com.rwawrzyniak.securephotos.encryption.usecase

import androidx.security.crypto.EncryptedSharedPreferences
import com.rwawrzyniak.securephotos.core.android.Constants
import com.rwawrzyniak.securephotos.core.android.ext.fromBase64
import com.rwawrzyniak.securephotos.core.android.ext.toBase64
import com.rwawrzyniak.securephotos.encryption.CryptoParameters
import com.rwawrzyniak.securephotos.encryption.SecretKeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject

class FindOrCreateKeyUseCase @Inject constructor(
	private val secretKeyGenerator: SecretKeyGenerator,
	private val sharedPreferences: EncryptedSharedPreferences
) {

    fun findOrCreateKey(): SecretKey =
        if (sharedPreferences.contains(CryptoParameters.ENCRYPTED_SECRET_SHARED_PREFERENCES_KEY)) {
			val base64EncodedSecretKey = requireNotNull(sharedPreferences.getString(
                CryptoParameters.ENCRYPTED_SECRET_SHARED_PREFERENCES_KEY, null
			))
            SecretKeySpec(
                base64EncodedSecretKey.fromBase64(),
                CryptoParameters.ENCRYPTION_ALGORITHM_AES
            )
		} else {
			var plainPassword = requireNotNull(sharedPreferences.getString(Constants.ENCRYPTED_PREFS_APP_CODE_KEY, null))
			val key = secretKeyGenerator.createSecretKey(plainPassword)
			sharedPreferences.edit().putString(CryptoParameters.ENCRYPTED_SECRET_SHARED_PREFERENCES_KEY, key.encoded.toBase64()).apply()
			key
        }
}

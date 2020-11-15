package com.rwawrzyniak.securephotos.ui.main.appcode.usecase

import androidx.security.crypto.EncryptedSharedPreferences
import com.rwawrzyniak.securephotos.core.android.Constants
import javax.inject.Inject

class AppCodeCheckUseCase @Inject internal constructor(
	private val preferences: EncryptedSharedPreferences
) {
	init {
		// We set hardcoded password, it could be extended in future to also set password.
		if (preferences.contains(Constants.ENCRYPTED_PREFS_APP_CODE_KEY).not())
			preferences.edit().putString(
				Constants.ENCRYPTED_PREFS_APP_CODE_KEY,
				Constants.PLAIN_TEXT_PASSWORD
			).apply()
	}

	fun isCorrectAppCode(givenPassword: String): Boolean {
		val correctPassword =
			requireNotNull(preferences.getString(Constants.ENCRYPTED_PREFS_APP_CODE_KEY, null))
		return givenPassword == correctPassword
	}
}

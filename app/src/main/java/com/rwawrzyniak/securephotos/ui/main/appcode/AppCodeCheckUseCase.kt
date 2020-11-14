package com.rwawrzyniak.securephotos.ui.main.appcode

import androidx.security.crypto.EncryptedSharedPreferences
import com.rwawrzyniak.securephotos.Constants.ENCRYPTED_PREFS_APP_CODE_KEY
import com.rwawrzyniak.securephotos.Constants.PLAIN_TEXT_PASSWORD
import javax.inject.Inject

class AppCodeCheckUseCase @Inject internal constructor(
	private val preferences: EncryptedSharedPreferences
){
	init {
		// We set hardcoded password, it could be extended in future to also set password.
		if(preferences.contains(ENCRYPTED_PREFS_APP_CODE_KEY).not())
			preferences.edit().putString(ENCRYPTED_PREFS_APP_CODE_KEY, PLAIN_TEXT_PASSWORD).apply()
	}

	fun isCorrectAppCode(givenPassword: String): Boolean {
		val correctPassword = requireNotNull(preferences.getString(ENCRYPTED_PREFS_APP_CODE_KEY, null))
		return givenPassword == correctPassword
	}
}

package com.rwawrzyniak.securephotos.ui.main.encryption

import androidx.annotation.VisibleForTesting
import java.security.Key
import javax.inject.Inject

class FindOrCreateKeyUseCase @Inject @VisibleForTesting internal constructor(
	private val androidKeyStore: AndroidKeyStore
) {

    fun findOrCreateKey(alias: String): Key =
        if (androidKeyStore.keyExists(alias)) {
            androidKeyStore.retrieveKey(alias)
        } else {
            androidKeyStore.createKey(alias)
        }
}

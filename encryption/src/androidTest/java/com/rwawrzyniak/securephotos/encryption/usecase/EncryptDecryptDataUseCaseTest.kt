package com.rwawrzyniak.securephotos.encryption.usecase

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import androidx.test.platform.app.InstrumentationRegistry
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.rwawrzyniak.securephotos.core.android.Constants
import com.rwawrzyniak.securephotos.encryption.AESInitializer
import com.rwawrzyniak.securephotos.encryption.SecretKeyGenerator
import org.junit.Before
import org.junit.Test

// todo replace it with @HiltAndroidTest
internal class EncryptDecryptDataUseCaseTest {

	private lateinit var aesInitializer: AESInitializer
	private lateinit var findOrCreateKeyUseCase: FindOrCreateKeyUseCase
	private lateinit var secretKeyGenerator: SecretKeyGenerator
	private lateinit var sharedPreferences: EncryptedSharedPreferences

	@Before
	fun setUp() {
		aesInitializer = AESInitializer()
		secretKeyGenerator = SecretKeyGenerator()
		sharedPreferences = sharedPreferences(InstrumentationRegistry.getInstrumentation().targetContext)
		findOrCreateKeyUseCase = FindOrCreateKeyUseCase(secretKeyGenerator, sharedPreferences)

		initializeSharedPrefWithPassword()
	}

	@Test
	fun shouldEncryptThenDecrypt(){
		val sut = sut()
		val plainByteArray = "testPayload".toByteArray()
		val encryptedByteArray = sut.encrypt(plainByteArray)
		val decryptedByteArray  = sut.decrypt(encryptedByteArray)
		assertThat(decryptedByteArray).isEqualTo(plainByteArray)
	}

	private fun sut() = EncryptDecryptDataUseCase(findOrCreateKeyUseCase, aesInitializer)

	// todo this is already defied i camera module use @HiltAndroidTest
	private fun sharedPreferences(context: Context) =
		EncryptedSharedPreferences.create(
			"testPrefs",
			MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
			context,
			EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
			EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
		) as EncryptedSharedPreferences

	private fun initializeSharedPrefWithPassword() {
		if (sharedPreferences.contains(Constants.ENCRYPTED_PREFS_APP_CODE_KEY).not())
			sharedPreferences.edit().putString(
				Constants.ENCRYPTED_PREFS_APP_CODE_KEY,
				Constants.PLAIN_TEXT_PASSWORD
			).apply()
	}
}

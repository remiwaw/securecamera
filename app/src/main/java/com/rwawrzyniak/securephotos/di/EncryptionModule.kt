package com.rwawrzyniak.securephotos.di

import android.content.Context
import android.content.SharedPreferences
import android.hardware.display.DisplayManager
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.rwawrzyniak.securephotos.ui.main.encryption.FindOrCreateKeyUseCase
import com.rwawrzyniak.securephotos.ui.main.encryption.SecretKeyGenerator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object EncryptionModule {

	@Singleton
	@Provides
	fun provideDisplayManager(
		@ApplicationContext context: Context,
		secretKeyGenerator: SecretKeyGenerator,
		encryptedSharedPreferences: EncryptedSharedPreferences
	): FindOrCreateKeyUseCase {
		return FindOrCreateKeyUseCase(context, secretKeyGenerator, encryptedSharedPreferences)
	}

	@Singleton
	@Provides
	fun provideSecureSharedPreferences(@ApplicationContext context: Context): EncryptedSharedPreferences {
		return EncryptedSharedPreferences.create(
			ENCRYPTED_PREFS_FILE_NAME,
			MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
			context,
			EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
			EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
		) as EncryptedSharedPreferences
	}

	private const val ENCRYPTED_PREFS_FILE_NAME = "safe_preferences"
}

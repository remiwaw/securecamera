package com.rwawrzyniak.securephotos.di

import android.content.Context
import android.hardware.display.DisplayManager
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
	fun provideDisplayManager(@ApplicationContext context: Context, secretKeyGenerator: SecretKeyGenerator): FindOrCreateKeyUseCase {
		return FindOrCreateKeyUseCase(context, secretKeyGenerator)
	}
}

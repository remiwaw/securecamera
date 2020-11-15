package com.rwawrzyniak.securephotos.di

import android.content.Context
import com.rwawrzyniak.securephotos.storage.FileImageProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object FileStorageModule {

	@Singleton
	@Provides
	fun provideFileStorage(
		@ApplicationContext context: Context
	): FileImageProvider = FileImageProvider(context)

	private const val ENCRYPTED_PREFS_FILE_NAME = "safe_preferences"
}

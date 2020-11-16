package com.rwawrzyniak.securephotos.di

import android.content.Context
import com.rwawrzyniak.securephotos.storage.FileImageProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import me.echodev.resizer.Resizer
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object UtilModule {

	@Singleton
	@Provides
	fun provideImageResizer(
		@ApplicationContext context: Context
	): Resizer = Resizer(context)
}

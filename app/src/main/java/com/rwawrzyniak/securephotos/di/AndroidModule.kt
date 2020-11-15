package com.rwawrzyniak.securephotos.di

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.view.inputmethod.InputMethodManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AndroidModule {

	@Singleton
	@Provides
	fun provideResources(@ApplicationContext context: Context): Resources {
		return context.resources
	}

	@Singleton
	@Provides
	fun provideInputMethodManager(@ApplicationContext context: Context): InputMethodManager {
		 return context.getSystemService(
			Activity.INPUT_METHOD_SERVICE
		) as InputMethodManager
	}

}

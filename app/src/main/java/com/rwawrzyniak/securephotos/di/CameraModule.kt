package com.rwawrzyniak.securephotos.di

import android.content.Context
import androidx.camera.lifecycle.ProcessCameraProvider
import com.google.common.util.concurrent.ListenableFuture
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object CameraModule {
	@Singleton
	@Provides
	fun provideProcessCameraProvider(context: Context): ListenableFuture<ProcessCameraProvider> {
		return ProcessCameraProvider.getInstance(context)
	}
}

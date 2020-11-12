package com.rwawrzyniak.securephotos.di

import android.content.Context
import android.hardware.display.DisplayManager
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import com.rwawrzyniak.securephotos.ui.main.previewphotos.datasource.ImagesDao
import com.rwawrzyniak.securephotos.ui.main.takepicture.usecase.CreateImageCaptureStorageOptions
import com.rwawrzyniak.securephotos.ui.main.takepicture.usecase.StartCameraUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object CameraModule {
	@Singleton
	@Provides
	fun provideProcessCameraProvider(@ApplicationContext context: Context): ProcessCameraProvider {
		return ProcessCameraProvider.getInstance(context).get()
	}

	@Provides
	fun provideDisplayManager(@ApplicationContext context: Context): DisplayManager {
		return context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
	}

//	@Provides
//	@ActivityScoped
//	fun provideAdapterFragmentState(@ActivityContext context: Context): AdapterFragmentState {
//		return AdapterFragmentState(context)
//	}
}

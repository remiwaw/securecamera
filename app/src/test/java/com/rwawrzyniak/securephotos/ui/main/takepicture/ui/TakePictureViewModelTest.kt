package com.rwawrzyniak.securephotos.ui.main.takepicture.ui

import android.os.Build
import com.rwawrzyniak.securephotos.core.test.test
import com.rwawrzyniak.securephotos.ui.main.appcode.ui.AppCodeViewModel
import com.rwawrzyniak.securephotos.ui.main.appcode.ui.AppCodeViewModelImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1]) // some magic to run robolectric in Java 8 / not 9
internal class TakePictureViewModelTest{
	@Before
	fun setUp() {
		MockitoAnnotations.openMocks(this)
	}

	@Test
	fun shouldEmitEffectsOnActions(){
		val sut = sut()

		runBlockingTest {
			val effectObserver = sut.observeEffect().test(this)

			sut.onAction(TakePictureViewModel.TakePhotosViewAction.Initialize)

			sut.onAction(TakePictureViewModel.TakePhotosViewAction.TakePictureButtonClicked)

			effectObserver.assertValues(TakePictureViewModel.TakePictureViewEffect.StartCameraPreview,
				TakePictureViewModel.TakePictureViewEffect.TakePicture
			)

			effectObserver.finish()
		}
	}

	private fun sut() = TakePictureViewModelImpl()
}

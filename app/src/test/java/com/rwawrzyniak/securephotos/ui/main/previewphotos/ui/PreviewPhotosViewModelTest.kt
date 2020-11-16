package com.rwawrzyniak.securephotos.ui.main.previewphotos.ui

import android.content.res.Resources
import android.os.Build
import androidx.paging.PagingData
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.stub
import com.rwawrzyniak.securephotos.R
import com.rwawrzyniak.securephotos.core.test.app
import com.rwawrzyniak.securephotos.core.test.test
import com.rwawrzyniak.securephotos.data.ImagesPagingDataSource
import com.rwawrzyniak.securephotos.ui.main.previewphotos.datasource.mapper.ImageModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1]) // some magic to run robolectric in Java 8 / not 9
internal class PreviewPhotosViewModelTest {

	@Mock
	private lateinit var imagesPagingDataSource: ImagesPagingDataSource

	@Mock
	private lateinit var createPagerUseCase: CreatePagerUseCase

	private val flowMock: Flow<PagingData<ImageModel>> = MutableSharedFlow()

	private val resources: Resources = app().resources

	@Before
	fun setUp() {
		MockitoAnnotations.openMocks(this)
		createPagerUseCase.stub {
			on { createFlow(any(), any(), any(), any()) } doReturn flowMock
		}
	}

	@Test
	fun shouldWirePagingDataFlowOnInitalizeView() {

		val sut = sut()

		runBlockingTest {
			val stateObserver = sut.observeState().test(this)

			stateObserver.assertValues(
				PreviewPhotosViewModel.PreviewPhotosViewState(
					pagingDataFlow = flowMock,
					noPhotosAvailable = true,
					loadedImagesCountText = ""
				)
			)

			stateObserver.finish()
		}
	}

	@Test
	fun shouldPrepareLoadingFinishedViewState() {
		val sut = sut()
		val itemCount = 10

		runBlockingTest {
			val stateObserver = sut.observeState().test(this)

			sut.onAction(PreviewPhotosViewModel.PreviewPhotosViewAction.OnLoadingFinished(itemCount))
			stateObserver.assertValues(
				PreviewPhotosViewModel.PreviewPhotosViewState(
					pagingDataFlow = flowMock,
					noPhotosAvailable = true,
					loadedImagesCountText = ""
				),
				PreviewPhotosViewModel.PreviewPhotosViewState(
					pagingDataFlow = flowMock,
					noPhotosAvailable = false,
					loadedImagesCountText = resources.getString(
						R.string.item_count_header, itemCount
					)
				)
			)

			stateObserver.finish()
		}
	}


	@Test
	fun shouldPrepareLoadingErrorEffect() {

		val sut = sut()

		val loadingFailed = Throwable("Some terrible error!")

		runBlockingTest {
			val stateObserver = sut.observeState().test(this)
			val effectObserver = sut.observeEffect().test(this)

			sut.onAction(
				PreviewPhotosViewModel.PreviewPhotosViewAction.OnLoadingFailed(
					loadingFailed
				)
			)
			stateObserver.assertValues(
				PreviewPhotosViewModel.PreviewPhotosViewState(
					pagingDataFlow = flowMock,
					noPhotosAvailable = true,
					loadedImagesCountText = ""
				)
			)

			effectObserver.assertValues(
				PreviewPhotosViewModel.PreviewPhotosViewEffect.ShowToastError(
					requireNotNull(loadingFailed.message)
				)
			)

			stateObserver.finish()
			effectObserver.finish()
		}
	}

	private fun sut() =
		PreviewPhotosViewModelImpl(imagesPagingDataSource, resources, createPagerUseCase)
}

package com.rwawrzyniak.securephotos.ui.main.appcode.ui

import android.content.res.Resources
import android.os.Build
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.stub
import com.rwawrzyniak.securephotos.R
import com.rwawrzyniak.securephotos.core.test.app
import com.rwawrzyniak.securephotos.core.test.test
import com.rwawrzyniak.securephotos.ui.main.appcode.usecase.AppCodeCheckUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
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
internal class AppCodeViewModelTest {

	@Mock
	private lateinit var useCase: AppCodeCheckUseCase
	private val resources: Resources = app().resources

	@Before
	fun setUp() {
		MockitoAnnotations.openMocks(this)
	}

	@Test
	fun shouldCalculateDefaultStateWithNoErrorOnStart(){
		val sut = sut()

		runBlockingTest {
			sut.observeState()
				.test(this)
				.assertValues(AppCodeViewModel.AppCodeViewState())
				.finish()
		}
	}

	@Test
	fun shouldCalculateErrorStateOnWrongAppCode(){
		val sut = sut()

		useCase.stub {
			on { isCorrectAppCode("wrongAppCode") } doReturn false
		}

		runBlockingTest {
			val stateObserver = sut.observeState().test(this)

			stateObserver.assertValues(AppCodeViewModel.AppCodeViewState())

			sut.onAction(AppCodeViewModel.AppCodeViewAction.SubmitButtonClicked("wrongAppCode"))

			stateObserver.assertValues(
				AppCodeViewModel.AppCodeViewState(),
				AppCodeViewModel.AppCodeViewState(errorText = resources.getString(R.string.incorrectPasswordMessage))
			)

			stateObserver.finish()
		}
	}

	@Test
	fun shouldCalculateNoErrorStateOnCorrectAppCodeAndEmitGoBackEffect(){
		val sut = sut()

		useCase.stub {
			on { isCorrectAppCode("correctAppCode") } doReturn true
		}

		runBlockingTest {
			val stateObserver = sut.observeState().test(this)
			val effectObserver = sut.observeEffect().test(this)

			stateObserver.assertValues(AppCodeViewModel.AppCodeViewState())

			sut.onAction(AppCodeViewModel.AppCodeViewAction.SubmitButtonClicked("correctAppCode"))

			effectObserver.assertValues(AppCodeViewModel.AppCodeViewEffect.NavigateToPreviousScreen)

			stateObserver.finish()
			effectObserver.finish()
		}
	}

	private fun sut() = AppCodeViewModelImpl(useCase, resources)
}

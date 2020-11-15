package com.rwawrzyniak.securephotos

import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.rwawrzyniak.securephotos.ui.main.appcode.ui.AppCodeFragment
import com.rwawrzyniak.securephotos.ui.main.previewphotos.ui.ImagesGridAdapter
import com.rwawrzyniak.securephotos.ui.main.previewphotos.ui.ImagesLoadStateAdapter
import com.rwawrzyniak.securephotos.ui.main.previewphotos.ui.PreviewPhotosFragment
import com.rwawrzyniak.securephotos.ui.main.takepicture.ui.TakePictureFragment
import com.rwawrzyniak.securephotos.ui.main.takepicture.usecase.UseCameraUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
class CustomFragmentFactory @Inject constructor(
	private val useCameraUseCase: UseCameraUseCase,
	private val imagesGridAdapter: ImagesGridAdapter,
	private val loadStateAdapter: ImagesLoadStateAdapter,
	private val inputMethodManager: InputMethodManager
) : FragmentFactory() {
	override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
		return when (className) {
			TakePictureFragment::class.java.name -> TakePictureFragment(useCameraUseCase)
			PreviewPhotosFragment::class.java.name -> PreviewPhotosFragment(
				imagesGridAdapter,
				loadStateAdapter
			)
			AppCodeFragment::class.java.name -> AppCodeFragment(inputMethodManager)
			else -> super.instantiate(classLoader, className)
		}
	}
}

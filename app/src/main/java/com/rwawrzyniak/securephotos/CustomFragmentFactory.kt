package com.rwawrzyniak.securephotos

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.rwawrzyniak.securephotos.ui.main.previewphotos.ui.ImagesGridAdapter
import com.rwawrzyniak.securephotos.ui.main.previewphotos.ui.PreviewPhotosFragment
import com.rwawrzyniak.securephotos.ui.main.takepicture.ui.TakePictureFragment
import com.rwawrzyniak.securephotos.ui.main.takepicture.usecase.StartCameraUseCase
import javax.inject.Inject

class CustomFragmentFactory @Inject constructor(private val startCameraUseCase: StartCameraUseCase, private val imagesGridAdapter: ImagesGridAdapter) : FragmentFactory(){
	override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
		return when(className){
			TakePictureFragment::class.java.name -> TakePictureFragment(startCameraUseCase)
			PreviewPhotosFragment::class.java.name -> PreviewPhotosFragment(imagesGridAdapter)
			else -> super.instantiate(classLoader, className)
		}
	}
}

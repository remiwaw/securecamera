package com.rwawrzyniak.securephotos

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.rwawrzyniak.securephotos.core.android.BasicFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainFragment : BasicFragment(R.layout.fragment_main) {

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		goToTakePhoto.setOnClickListener {
			findNavController().navigate(R.id.action_mainFragment_to_takePictureFragment)
		}

		goToViewPhotos.setOnClickListener {
			findNavController().navigate(R.id.action_mainFragment_to_previewPhotosFragment)
		}
	}
}

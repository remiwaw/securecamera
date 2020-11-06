package com.rwawrzyniak.securephotos.ui.main

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.rwawrzyniak.securephotos.R
import com.rwawrzyniak.securephotos.ui.main.permissions.CameraPermissionFragment
import com.rwawrzyniak.securephotos.ui.main.permissions.CameraPermissionFragment.Companion.createAndCommitPermissionFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.take_picture_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TakePictureFragment : Fragment(R.layout.take_picture_fragment) {

	private val viewModel: TakePictureViewModelImpl by viewModels()

	private lateinit var startCameraUseCase: StartCameraUseCase
	private lateinit var permissionFragment: CameraPermissionFragment

	@ExperimentalCoroutinesApi
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		// If user comes to this screen we ask for permission
		// if not given we show toast and navigate back
		// if given we startCamera

		startCameraUseCase = StartCameraUseCase(
			CreateImageCaptureStorageOptions(requireContext()),
			requireContext(),
			this
		)

		permissionFragment = createAndCommitPermissionFragment(CAMERA_PERMISSION_FRAGMENT_TAG)

		CoroutineScope(Main).launch {
			if(permissionFragment.checkPermission()){
				startCameraUseCase.startCamera(viewFinder)
			} else {
				Toast.makeText(requireContext(), "You have to grant camera permission, to use this app", Toast.LENGTH_SHORT).show()
				findNavController().popBackStack()
			}
		}

		camera_capture_button.setOnClickListener {
			CoroutineScope(IO).launch {
				viewModel.onAction(TakePictureViewAction.TakePhoto)
			}
		}

		CoroutineScope(Main).launch{
			viewModel.observeEffect()
				.collect {
					when(it){
						TakePictureViewEffect.TakePicture -> startCameraUseCase.takePicture(viewFinder)
					}
				}
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		// TODO clear camera?
	}

	companion object{
		private const val CAMERA_PERMISSION_FRAGMENT_TAG = "CAMERA_PERMISSION_FRAGMENT_TAG"
	}
}

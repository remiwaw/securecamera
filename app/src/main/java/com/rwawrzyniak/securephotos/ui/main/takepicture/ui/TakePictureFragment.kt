package com.rwawrzyniak.securephotos.ui.main.takepicture.ui

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rwawrzyniak.securephotos.R
import com.rwawrzyniak.securephotos.ui.main.permissions.PermissionFragment
import com.rwawrzyniak.securephotos.ui.main.permissions.PermissionFragment.Companion.createAndCommitPermissionFragment
import com.rwawrzyniak.securephotos.ui.main.previewphotos.datasource.FileImageProvider
import com.rwawrzyniak.securephotos.ui.main.previewphotos.datasource.ImagesDao
import com.rwawrzyniak.securephotos.ui.main.takepicture.ui.TakePictureViewModel.TakePhotosViewAction.TakePhoto
import com.rwawrzyniak.securephotos.ui.main.takepicture.usecase.CreateImageCaptureStorageOptions
import com.rwawrzyniak.securephotos.ui.main.takepicture.usecase.StartCameraUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.take_picture_fragment.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TakePictureFragment : Fragment(R.layout.take_picture_fragment) {

	private val viewModel: TakePictureViewModelImpl by viewModels()

	private lateinit var startCameraUseCase: StartCameraUseCase
	private lateinit var andStoragePermissionFragment: PermissionFragment
	private val imagesDao = ImagesDao(FileImageProvider())

	@ExperimentalCoroutinesApi
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		andStoragePermissionFragment = createAndCommitPermissionFragment(CAMERA_PERMISSION_FRAGMENT_TAG, REQUIRED_PERMISSIONS)

		startCameraUseCase = StartCameraUseCase(
			CreateImageCaptureStorageOptions(requireContext()),
			requireContext(),
			this,
			imagesDao
		)

		setupUI()
		wireUpViewModel()
	}

	private fun setupUI() {
		camera_capture_button.setOnClickListener {
			viewModel.onAction(TakePhoto)
		}
	}

	private fun wireUpViewModel() {
		viewModel.observeState()
			.onEach { state -> handleStateChanges(state) }
			.launchIn(lifecycleScope)
	}

	private suspend fun handleStateChanges(state: TakePictureViewModel.TakePictureViewState) {
		val context = requireContext()
		if(!andStoragePermissionFragment.checkPermission()){
			showPermissionPernamentlyDeniedPopup(context)
			findNavController().popBackStack()
		}
		when {
			state.isCheckPermission -> startCameraUseCase.startCamera(viewFinder)
			state.isTakingPicture -> startCameraUseCase.takePicture(viewFinder)
		}
	}

	private fun showPermissionPernamentlyDeniedPopup(context: Context) {
		Toast.makeText(
			context,
			context.getString(R.string.storage_and_camera_permission_denied_permanently),
			Toast.LENGTH_LONG
		).show()
	}

	override fun onDestroy() {
		super.onDestroy()
		// TODO clear camera?
	}

	companion object{
		private const val CAMERA_PERMISSION_FRAGMENT_TAG = "CAMERA_PERMISSION_FRAGMENT_TAG"
		private val REQUIRED_PERMISSIONS = arrayOf(
			Manifest.permission.CAMERA,
			Manifest.permission.WRITE_EXTERNAL_STORAGE
		)

	}
}

package com.rwawrzyniak.securephotos.ui.main.takepicture.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.rwawrzyniak.securephotos.R
import com.rwawrzyniak.securephotos.ui.main.takepicture.usecase.CreateImageCaptureStorageOptions
import com.rwawrzyniak.securephotos.ui.main.takepicture.usecase.StartCameraUseCase
import com.rwawrzyniak.securephotos.ui.main.previewphotos.datasource.FileImageProvider
import com.rwawrzyniak.securephotos.ui.main.permissions.CameraAndStoragePermissionFragment
import com.rwawrzyniak.securephotos.ui.main.permissions.CameraAndStoragePermissionFragment.Companion.createAndCommitPermissionFragment
import com.rwawrzyniak.securephotos.ui.main.previewphotos.datasource.ImagesDao
import com.rwawrzyniak.securephotos.ui.main.previewphotos.ui.ImagesLoadStateAdapter
import com.rwawrzyniak.securephotos.ui.main.previewphotos.ui.PreviewPhotosViewModel
import com.rwawrzyniak.securephotos.ui.main.takepicture.ui.TakePictureViewModel.TakePhotosViewAction.TakePhoto
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.preview_photos_fragment.*
import kotlinx.android.synthetic.main.take_picture_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TakePictureFragment : Fragment(R.layout.take_picture_fragment) {

	private val viewModel: TakePictureViewModelImpl by viewModels()

	private lateinit var startCameraUseCase: StartCameraUseCase
	private lateinit var andStoragePermissionFragment: CameraAndStoragePermissionFragment
	private val imagesDao = ImagesDao(FileImageProvider())

	@ExperimentalCoroutinesApi
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
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

		when {
			state.isCheckPermission -> {
				andStoragePermissionFragment = createAndCommitPermissionFragment(CAMERA_PERMISSION_FRAGMENT_TAG)
				if(andStoragePermissionFragment.checkPermission()){
					startCameraUseCase.startCamera(viewFinder)
				} else {
					Toast.makeText(
						context,
						context.getString(R.string.camera_permission_denied_permanently),
						Toast.LENGTH_LONG
					).show()
					findNavController().popBackStack()
				}
			}
			state.isTakingPicture -> {
				if(andStoragePermissionFragment.checkPermission()){
					startCameraUseCase.takePicture(viewFinder)
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

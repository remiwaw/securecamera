package com.rwawrzyniak.securephotos.ui.main.takepicture.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.rwawrzyniak.securephotos.R
import com.rwawrzyniak.securephotos.ui.main.takepicture.usecase.CreateImageCaptureStorageOptions
import com.rwawrzyniak.securephotos.ui.main.takepicture.usecase.StartCameraUseCase
import com.rwawrzyniak.securephotos.ui.main.previewphotos.datasource.FileImageProvider
import com.rwawrzyniak.securephotos.ui.main.permissions.CameraPermissionFragment
import com.rwawrzyniak.securephotos.ui.main.permissions.CameraPermissionFragment.Companion.createAndCommitPermissionFragment
import com.rwawrzyniak.securephotos.ui.main.previewphotos.datasource.ImagesDao
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
	private val imagesDao = ImagesDao(FileImageProvider())

	@ExperimentalCoroutinesApi
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		val context = requireContext()

		startCameraUseCase = StartCameraUseCase(
			CreateImageCaptureStorageOptions(context),
			context,
			this,
			imagesDao
		)

		permissionFragment = createAndCommitPermissionFragment(CAMERA_PERMISSION_FRAGMENT_TAG)

		CoroutineScope(Main).launch {
			if(permissionFragment.checkPermission()){
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

package com.rwawrzyniak.securephotos.ui.main.takepicture.ui

import android.Manifest
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.camera.view.PreviewView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rwawrzyniak.securephotos.R
import com.rwawrzyniak.securephotos.core.android.BasicFragment
import com.rwawrzyniak.securephotos.ui.main.permissions.PermissionFragment
import com.rwawrzyniak.securephotos.ui.main.permissions.PermissionFragment.Companion.createAndCommitPermissionFragment
import com.rwawrzyniak.securephotos.ui.main.takepicture.ui.TakePictureViewModel.TakePhotosViewAction.TakePictureButtonClicked
import com.rwawrzyniak.securephotos.ui.main.takepicture.usecase.UseCameraUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.camera_ui_container.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TakePictureFragment @Inject constructor(private val useCameraUseCase: UseCameraUseCase) : BasicFragment(R.layout.take_picture_fragment) {

	private lateinit var container: ConstraintLayout
	private lateinit var previewView: PreviewView

	private val viewModel: TakePictureViewModelImpl by viewModels()
	private lateinit var permissionFragment: PermissionFragment
	private var shouldSkipAppCode = false

	@ExperimentalCoroutinesApi
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		container = view as ConstraintLayout
		previewView = container.findViewById(R.id.viewFinder)

		permissionFragment = createAndCommitPermissionFragment(CAMERA_PERMISSION_FRAGMENT_TAG, REQUIRED_PERMISSIONS)
		shouldSkipAppCode = permissionFragment.shouldSkipAppCode()
		setupUI()

		wireUpViewModel()
	}

	override fun onResume() {
		super.onResume()
		shouldSkipAppCode = permissionFragment.shouldSkipAppCode()
	}

	override fun shouldSkipAppCode(): Boolean {
		return shouldSkipAppCode
	}

	override fun onConfigurationChanged(newConfig: Configuration) {
		super.onConfigurationChanged(newConfig)
		updateCameraUi()
	}

	private fun setupUI() {
		previewView.post {
			updateCameraUi()
		}
	}

	private fun updateCameraUi() {
		camera_ui_container?.let {
			container.removeView(it)
		}

		val controls = View.inflate(requireContext(), R.layout.camera_ui_container, container)

		controls.findViewById<ImageButton>(R.id.camera_capture_button).setOnClickListener {
			viewModel.onAction(TakePictureButtonClicked)
		}
	}

	private fun wireUpViewModel() {
		lifecycleScope.launch {
			viewModel.observeEffect()
				.collectLatest { effect -> handleEffectChange(effect) }
		}

		viewModel.onAction(TakePictureViewModel.TakePhotosViewAction.Initialize)
	}

	private suspend fun handleEffectChange(effect: TakePictureViewModel.TakePictureViewEffect) {
		if(!permissionFragment.checkPermission()){
			showPermissionPermanentlyDeniedPopup()
			findNavController().popBackStack()
		}

		when(effect){
			TakePictureViewModel.TakePictureViewEffect.TakePicture -> {
				val result = useCameraUseCase.takePicture(previewView, this).await()
				showResultToast(result)
			}

			TakePictureViewModel.TakePictureViewEffect.StartCameraPreview -> {
				useCameraUseCase.registerLifecycle(lifecycle)
				useCameraUseCase.startCamera(previewView, this)
			}
		}
	}

	private fun showPermissionPermanentlyDeniedPopup() {
		Toast.makeText(
			requireContext(),
			requireContext().getString(R.string.storage_and_camera_permission_denied_permanently),
			Toast.LENGTH_LONG
		).show()
	}

	private fun showResultToast(message: String) {
		Toast.makeText(
			requireContext(),
			message,
			Toast.LENGTH_LONG
		).show()
	}

	companion object{
		private const val CAMERA_PERMISSION_FRAGMENT_TAG = "CAMERA_PERMISSION_FRAGMENT_TAG"
		private val REQUIRED_PERMISSIONS = arrayOf(
			Manifest.permission.CAMERA,
			Manifest.permission.WRITE_EXTERNAL_STORAGE
		)
	}
}

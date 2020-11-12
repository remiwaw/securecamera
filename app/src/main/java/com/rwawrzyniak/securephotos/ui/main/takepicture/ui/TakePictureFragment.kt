package com.rwawrzyniak.securephotos.ui.main.takepicture.ui

import android.Manifest
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.camera.view.PreviewView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rwawrzyniak.securephotos.R
import com.rwawrzyniak.securephotos.ui.main.permissions.PermissionFragment
import com.rwawrzyniak.securephotos.ui.main.permissions.PermissionFragment.Companion.createAndCommitPermissionFragment
import com.rwawrzyniak.securephotos.ui.main.takepicture.ui.TakePictureViewModel.TakePhotosViewAction.TakePictureButtonClicked
import com.rwawrzyniak.securephotos.ui.main.takepicture.usecase.StartCameraUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.camera_ui_container.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TakePictureFragment @Inject constructor(private val startCameraUseCase: StartCameraUseCase) : Fragment(R.layout.take_picture_fragment) {

	private lateinit var container: ConstraintLayout
	private lateinit var viewFinder: PreviewView

	private val viewModel: TakePictureViewModelImpl by viewModels()
	private lateinit var permissionFragment: PermissionFragment

	@ExperimentalCoroutinesApi
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		container = view as ConstraintLayout
		viewFinder = container.findViewById(R.id.viewFinder)

		permissionFragment = createAndCommitPermissionFragment(CAMERA_PERMISSION_FRAGMENT_TAG, REQUIRED_PERMISSIONS)
		setupUI()

		wireUpViewModel()
	}

	override fun onConfigurationChanged(newConfig: Configuration) {
		super.onConfigurationChanged(newConfig)
		updateCameraUi()
	}

	private fun setupUI() {
		viewFinder.post {
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
		viewModel.observeEffect()
			.onEach { effect -> handleEffectChange(effect) }
			.launchIn(lifecycleScope)

		viewModel.onAction(TakePictureViewModel.TakePhotosViewAction.Initialize)
	}

	private suspend fun handleEffectChange(effect: TakePictureViewModel.TakePictureViewEffect) {
		if(!permissionFragment.checkPermission()){
			showPermissionPermanentlyDeniedPopup(requireContext())
			findNavController().popBackStack()
		}
		when(effect){
			TakePictureViewModel.TakePictureViewEffect.TakePicture -> {
				val result = startCameraUseCase.takePicture(viewFinder, this).await()
				showResultToast(result)
			}

			TakePictureViewModel.TakePictureViewEffect.StartCameraPreview -> {
				startCameraUseCase.registerLifecycle(lifecycle)
				startCameraUseCase.startCamera(viewFinder, this)
			}
		}
	}

	private fun showPermissionPermanentlyDeniedPopup(context: Context) {
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

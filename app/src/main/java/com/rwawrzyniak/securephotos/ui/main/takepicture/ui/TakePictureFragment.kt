package com.rwawrzyniak.securephotos.ui.main.takepicture.ui

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
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
import kotlinx.android.synthetic.main.take_picture_fragment.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TakePictureFragment @Inject constructor(private val startCameraUseCase: StartCameraUseCase) : Fragment(R.layout.take_picture_fragment) {

	private val viewModel: TakePictureViewModelImpl by viewModels()

	private lateinit var andStoragePermissionFragment: PermissionFragment

	@ExperimentalCoroutinesApi
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		andStoragePermissionFragment = createAndCommitPermissionFragment(CAMERA_PERMISSION_FRAGMENT_TAG, REQUIRED_PERMISSIONS)
		setupUI()
		wireUpViewModel()
	}

	override fun onPause() {
		super.onPause()
		Log.i("TOTO", "onPause")
	}

	override fun onResume() {
		super.onResume()
		Log.i("TOTO", "onResume")
	}

	private fun setupUI() {
		camera_capture_button.setOnClickListener {
			viewModel.onAction(TakePictureButtonClicked)
		}
	}

	private fun wireUpViewModel() {
		viewModel.observeEffect()
			.onEach { effect -> handleEffectChange(effect) }
			.catch { Log.e("TOTO", it.message, it) }
			.onCompletion { Log.i("TOTO", "observeEffectCompleted") }
			.launchIn(lifecycleScope)

		viewModel.onAction(TakePictureViewModel.TakePhotosViewAction.Initialize)
	}

	private suspend fun handleEffectChange(effect: TakePictureViewModel.TakePictureViewEffect) {
		if(!andStoragePermissionFragment.checkPermission()){
			showPermissionPermanentlyDeniedPopup(requireContext())
			findNavController().popBackStack()
		}
		when(effect){
			TakePictureViewModel.TakePictureViewEffect.TakePicture -> {
				Log.i("TOTO"," takePicture effect executed")
//				startCameraUseCase.takePicture(viewFinder, this)
			}

			TakePictureViewModel.TakePictureViewEffect.CheckPermissions -> {
				Log.i("TOTO"," check permission effect executed")
				startCameraUseCase.startCamera(viewFinder, this)
			}
		}
	}

	private fun showPermissionPermanentlyDeniedPopup(context: Context) {
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

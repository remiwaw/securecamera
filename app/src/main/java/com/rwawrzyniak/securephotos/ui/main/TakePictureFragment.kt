package com.rwawrzyniak.securephotos.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.rwawrzyniak.securephotos.R
import kotlinx.android.synthetic.main.take_picture_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class TakePictureFragment : Fragment(R.layout.take_picture_fragment) {
	private lateinit var imageCapture: ImageCapture
	private lateinit var outputDirectory: File
	private lateinit var cameraExecutor: ExecutorService

	private val viewModel: TakePictureViewModel by viewModels()

	private val actions = BroadcastChannel<TakePictureViewAction>(1)

	@ExperimentalCoroutinesApi
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		camera_capture_button.setOnClickListener {
			CoroutineScope(IO).launch {
				actions.send(TakePictureViewAction.TakePhoto)
			}
		}

		// Request camera permissions
		if (allPermissionsGranted()) {
			startCamera()
		} else {
			requestPermissions(
				REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
			)
		}

		// Set up the listener for take photo button
		camera_capture_button.setOnClickListener { takePhoto() }

		outputDirectory = getOutputDirectory()

		cameraExecutor = Executors.newSingleThreadExecutor()

	}

	private fun takePhoto() {
		// Create time-stamped output file to hold the image
		val photoFile = File(
			outputDirectory,
			SimpleDateFormat(
				FILENAME_FORMAT, Locale.US
			).format(System.currentTimeMillis()) + ".jpg"
		)

		// Create output options object which contains file + metadata
		val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

		// Set up image capture listener, which is triggered after photo has
		// been taken
		imageCapture.takePicture(
			outputOptions,
			ContextCompat.getMainExecutor(requireContext()),
			object : ImageCapture.OnImageSavedCallback {
				override fun onError(exc: ImageCaptureException) {
					Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
				}

				override fun onImageSaved(output: ImageCapture.OutputFileResults) {
					val savedUri = Uri.fromFile(photoFile)
					val msg = "Photo capture succeeded: $savedUri"
					Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
					Log.d(TAG, msg)
				}
			})
	}

	private fun startCamera() {
		val cameraProviderFuture = ProcessCameraProvider.getInstance(this.requireContext())

		cameraProviderFuture.addListener(Runnable {
			// Used to bind the lifecycle of cameras to the lifecycle owner
			val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

			// Preview
			val preview = Preview.Builder()
				.build()
				.also {
					it.setSurfaceProvider(viewFinder.surfaceProvider)
				}

			imageCapture = ImageCapture.Builder()
				.build()

			// Select back camera as a default
			val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

			try {
				// Unbind use cases before rebinding
				cameraProvider.unbindAll()

				// Bind use cases to camera
				cameraProvider.bindToLifecycle(
					this, cameraSelector, preview, imageCapture
				)

			} catch (exc: Exception) {
				Log.e(TAG, "Use case binding failed", exc)
			}

		}, ContextCompat.getMainExecutor(requireContext()))
	}

	private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
		ContextCompat.checkSelfPermission(
			this.requireContext(), it
		) == PackageManager.PERMISSION_GRANTED
	}

	private fun getOutputDirectory(): File {
		val mediaDir = requireContext().externalMediaDirs.firstOrNull()?.let {
			File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
		}
		return if (mediaDir != null && mediaDir.exists())
			mediaDir else requireContext().filesDir
	}

	override fun onDestroy() {
		super.onDestroy()
		cameraExecutor.shutdown()
	}

	override fun onRequestPermissionsResult(
		requestCode: Int, permissions: Array<String>, grantResults:
		IntArray
	) {
		if (requestCode == REQUEST_CODE_PERMISSIONS) {
			if (allPermissionsGranted()) {
				startCamera()
			} else {
				Toast.makeText(
					requireContext(),
					"Permissions not granted by the user.",
					Toast.LENGTH_SHORT
				).show()
			}
		}
	}

	companion object {
		private const val TAG = "CameraXBasic"
		private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
		private const val REQUEST_CODE_PERMISSIONS = 10
		private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
	}

}

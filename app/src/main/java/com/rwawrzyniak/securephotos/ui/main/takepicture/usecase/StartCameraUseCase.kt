package com.rwawrzyniak.securephotos.ui.main.takepicture.usecase

import android.content.Context
import android.hardware.display.DisplayManager
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.rwawrzyniak.securephotos.ui.main.previewphotos.datasource.ImagesDao
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.CompletableDeferred
import java.io.File
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

// source: https://danielecampogiani.com/blog/2020/08/card-scanner-on-android-using-camerax-and-mlkit/
class StartCameraUseCase @Inject constructor(
	private val createImageCaptureStorageOptions: CreateImageCaptureStorageOptions,
	@ActivityContext private val context: Context,
	private val imagesDao: ImagesDao,
	private val cameraProvider: ProcessCameraProvider,
	private val displayManager: DisplayManager
) : LifecycleObserver {
	private lateinit var cameraExecutor: ExecutorService
	private lateinit var imageCapture: ImageCapture
	private var previewView: PreviewView? = null
	private var displayId: Int = -1

	private val displayListener = object : DisplayManager.DisplayListener {
		override fun onDisplayAdded(displayId: Int) = Unit
		override fun onDisplayRemoved(displayId: Int) = Unit
		override fun onDisplayChanged(displayId: Int) = previewView?.let { view ->
			if (displayId == this@StartCameraUseCase.displayId) {
				imageCapture.targetRotation = view.display.rotation
			}
		} ?: Unit
	}

	init {
		displayManager.registerDisplayListener(displayListener, null)
	}

	fun registerLifecycle(lifecycle : Lifecycle){
		lifecycle.addObserver(this)
	}

	@OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
	fun onDestroyFragment() {
		// Shut down our background executor
		cameraExecutor.shutdown()
		displayManager.unregisterDisplayListener(displayListener)
	}

	@OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
	fun onFragmentCreated() {
		cameraExecutor = Executors.newSingleThreadExecutor()
	}


	fun startCamera(previewView: PreviewView, lifecycleOwner: LifecycleOwner){
		this.previewView = previewView
		imageCapture = bindUseCases(previewView, lifecycleOwner)
		displayId = previewView.display.displayId
	}

	fun takePicture(
		previewView: PreviewView,
		lifecycleOwner: LifecycleOwner
	): CompletableDeferred<String> {
		if(!::imageCapture.isInitialized){
			startCamera(previewView, lifecycleOwner)
		}
		return requireNotNull(imageCapture).takePicture(cameraExecutor)
	}

	private fun bindUseCases(
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner
	): ImageCapture {
		val preview = buildPreview(previewView.surfaceProvider)
		val cameraSelector = buildCameraSelector()
		val imageCapture = buildTakePicture(previewView)

		try {
			cameraProvider.unbindAll()
			cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture)
		} catch(exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
		}

		return imageCapture
	}

	private fun buildPreview(surfaceProvider: Preview.SurfaceProvider): Preview = Preview.Builder()
		.build()
		.apply {
			setSurfaceProvider(surfaceProvider)
		}

	private fun buildCameraSelector(): CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

	private fun buildTakePicture(previewView: PreviewView): ImageCapture = ImageCapture.Builder()
		.setTargetRotation(previewView.display.rotation)
		.build()


	private fun ImageCapture.takePicture(executor: Executor): CompletableDeferred<String> {

		val makePhotoResult = CompletableDeferred<String>()

		val createOutputOptionsAndFilePair: Pair<ImageCapture.OutputFileOptions, File> =
			createImageCaptureStorageOptions.createOutputOptions()

		takePicture(
			createOutputOptionsAndFilePair.first,
			executor,
			object : ImageCapture.OnImageCapturedCallback(),
				ImageCapture.OnImageSavedCallback {

				override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
					// This api is not clear, outputFileResults.savedUri is not null ONLY if file was saved using MediaStore
					val savedImage = createOutputOptionsAndFilePair.second
					imagesDao.save(savedImage.name, savedImage.readBytes())
					makePhotoResult.complete("ImageSaved" + savedImage.name)

					// We delete image, we want to save only encrypted version.
					savedImage.delete()
					Log.d(TAG, "image saved:${savedImage.name}")
				}

				override fun onCaptureSuccess(image: ImageProxy) {
					Log.d(TAG, "Photo capture sucess")
					super.onCaptureSuccess(image)
				}

				override fun onError(exception: ImageCaptureException) {
					Log.e(TAG, "Photo capture failed: ${exception.message}", exception)
					makePhotoResult.complete("Photo capture failed: ${exception.message}")

					// TODO handle exception
					super.onError(exception)
				}
			})

		return makePhotoResult
	}

	companion object {
		private const val TAG = "CameraXStartCamera"
	}
}

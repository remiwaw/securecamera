package com.rwawrzyniak.securephotos.ui.main

import android.content.Context
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.rwawrzyniak.securephotos.ui.main.previewphotos.datasource.ImagesDao
import java.io.File
import java.net.URI
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

// source: https://danielecampogiani.com/blog/2020/08/card-scanner-on-android-using-camerax-and-mlkit/
class StartCameraUseCase(
	private val createImageCaptureStorageOptions: CreateImageCaptureStorageOptions,
	private val context: Context,
	private val lifecycleOwner: LifecycleOwner,
	private val imagesDao: ImagesDao
) {

	private var imageCapture: ImageCapture? = null

	suspend fun startCamera(previewView: PreviewView){
		imageCapture = bindUseCases(context.getCameraProvider(), previewView, lifecycleOwner)
	}

	suspend fun takePicture(
		previewView: PreviewView
	){
		if(imageCapture == null){
			startCamera(previewView)
		}
		requireNotNull(imageCapture).takePicture(context.executor)
	}

	private fun bindUseCases(
		cameraProvider: ProcessCameraProvider,
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


	private suspend fun ImageCapture.takePicture(executor: Executor): ImageProxy {
		return suspendCoroutine { continuation ->

			val createOutputOptionsAndFilePair: Pair<ImageCapture.OutputFileOptions, File> = createImageCaptureStorageOptions.createOutputOptions()

			takePicture(createOutputOptionsAndFilePair.first, executor, object : ImageCapture.OnImageCapturedCallback(),
				ImageCapture.OnImageSavedCallback {

				override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
					// This api is not clear, outputFileResults.savedUri is not null ONLY if file was saved using MediaStore
					val savedImage = createOutputOptionsAndFilePair.second
					imagesDao.save(savedImage.name, savedImage.readBytes())
					// We delete image, we want to save only encrypted version.
					savedImage.delete()
					Log.d(TAG, "image saved:${savedImage.name}")
				}

				override fun onCaptureSuccess(image: ImageProxy) {
					Log.d(TAG, "Photo capture sucess")
					continuation.resume(image)
					super.onCaptureSuccess(image)
				}

				override fun onError(exception: ImageCaptureException) {
					Log.e(TAG, "Photo capture failed: ${exception.message}", exception)

					// TODO handle exception
					continuation.resumeWithException(exception)
					super.onError(exception)
				}
			})
		}
	}

	private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
		suspendCoroutine { continuation ->
			ProcessCameraProvider.getInstance(this).apply {
				addListener(Runnable {
					continuation.resume(get())
				}, executor)
			}
		}

	private val Context.executor: Executor
		get() = ContextCompat.getMainExecutor(this)

	companion object {
		private const val TAG = "CameraXStartCamera"
	}
}

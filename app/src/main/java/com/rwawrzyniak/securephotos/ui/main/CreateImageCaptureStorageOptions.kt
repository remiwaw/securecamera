package com.rwawrzyniak.securephotos.ui.main

import android.content.Context
import androidx.camera.core.ImageCapture
import com.rwawrzyniak.securephotos.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CreateImageCaptureStorageOptions(private val context: Context) {

	fun createOutputOptions(): ImageCapture.OutputFileOptions {
		val photoFile = File(
			getOutputDirectory(context),
			SimpleDateFormat(
				FILENAME_FORMAT, Locale.US
			).format(System.currentTimeMillis()) + ".jpg"
		)

		return ImageCapture.OutputFileOptions.Builder(photoFile).build()
	}

	private fun getOutputDirectory(context: Context): File {
		val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
			File(it, context.getString(R.string.app_name)).apply { mkdirs() }
		}
		return if (mediaDir != null && mediaDir.exists())
			mediaDir else context.filesDir
	}

	companion object{
		private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
	}
}
package com.rwawrzyniak.securephotos.ui.main.takepicture.usecase

import android.content.Context
import androidx.camera.core.ImageCapture
import com.rwawrzyniak.securephotos.R
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class CreateImageCaptureStorageOptions @Inject constructor
	(@ActivityContext private val context: Context) {

	fun createOutputOptions(): Pair<ImageCapture.OutputFileOptions, File> {
		val photoFile = File(
            getOutputDirectory(context),
            SimpleDateFormat(
                FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".jpg"
        )

		return Pair(ImageCapture.OutputFileOptions.Builder(photoFile).build(), photoFile)
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

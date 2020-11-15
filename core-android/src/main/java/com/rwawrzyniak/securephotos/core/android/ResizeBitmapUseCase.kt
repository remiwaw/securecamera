package com.rwawrzyniak.securephotos.core.android

import android.graphics.Bitmap
import javax.inject.Inject

// source: https://handyopinion.com/resize-bitmap-by-keeping-the-same-aspect-ratio-in-kotlin-android/
class ResizeBitmapUseCase @Inject constructor() {
	fun resizeBitmap(source: Bitmap, maxLength: Int): Bitmap {
		try {
			if (source.height >= source.width) {
				if (source.height <= maxLength) { // if image height already smaller than the required height
					return source
				}

				val aspectRatio = source.width.toDouble() / source.height.toDouble()
				val targetWidth = (maxLength * aspectRatio).toInt()
				val result = Bitmap.createScaledBitmap(source, targetWidth, maxLength, false)
				return result
			} else {
				if (source.width <= maxLength) { // if image width already smaller than the required width
					return source
				}

				val aspectRatio = source.height.toDouble() / source.width.toDouble()
				val targetHeight = (maxLength * aspectRatio).toInt()

				val result = Bitmap.createScaledBitmap(source, maxLength, targetHeight, false)
				return result
			}
		} catch (e: Exception) {
			return source
		}
	}
}

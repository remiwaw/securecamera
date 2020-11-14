package com.rwawrzyniak.securephotos.ui.main.previewphotos.datasource.mapper

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.rwawrzyniak.securephotos.core.android.EntityMapper
import com.rwawrzyniak.securephotos.core.android.ResizeBitmapUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@ExperimentalCoroutinesApi
class ByteArrayBitMapMapper @Inject constructor(private val resizeBitmapUseCase: ResizeBitmapUseCase): EntityMapper<ByteArray, Bitmap> {

	override fun mapFromEntity(byteArray: ByteArray): Bitmap {
		return BitmapFactory.decodeByteArray(
			byteArray,
			0,
			byteArray.size
		)
	}

	override fun mapToEntity(bitmap: Bitmap): ByteArray {
		val stream = ByteArrayOutputStream()
		val compressedBitmap = resizeBitmapUseCase.resizeBitmap(bitmap, MAX_LENGTH_OR_WIDTH)
		compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
		return stream.toByteArray()
	}

	companion object {
		private const val COMPRESSION_RATIO = 80
		private const val MAX_LENGTH_OR_WIDTH = 400

	}
}

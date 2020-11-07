package com.rwawrzyniak.securephotos.ui.main.previewphotos.datasource.mapper

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import javax.inject.Inject


// TODO introduce here decrypting! later
class ByteArrayBitMapMapper @Inject constructor(): EntityMapper<ByteArray, Bitmap> {

	override fun mapFromEntity(byteArray: ByteArray): Bitmap {
		return BitmapFactory.decodeByteArray(
			byteArray,
			0,
			byteArray.size
		)
	}

	override fun mapToEntity(bitmap: Bitmap): ByteArray {
		val stream = ByteArrayOutputStream()
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
		return stream.toByteArray()
	}
}

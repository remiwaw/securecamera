package com.rwawrzyniak.securephotos.ui.main.previewphotos.datasource.mapper

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.rwawrzyniak.securephotos.core.android.EntityMapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@ExperimentalCoroutinesApi
class ByteArrayBitMapMapper @Inject constructor() : EntityMapper<ByteArray, Bitmap> {

	override fun mapFromEntity(entity: ByteArray): Bitmap {
		return BitmapFactory.decodeByteArray(
			entity,
			0,
			entity.size
		)
	}

	override fun mapToEntity(domainModel: Bitmap): ByteArray {
		val stream = ByteArrayOutputStream()
		domainModel.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_RATIO, stream)
		return stream.toByteArray()
	}

	companion object {
		private const val COMPRESSION_RATIO = 80
	}
}

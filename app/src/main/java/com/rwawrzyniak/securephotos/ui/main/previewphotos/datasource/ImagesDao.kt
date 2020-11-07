package com.rwawrzyniak.securephotos.ui.main.previewphotos.datasource

import javax.inject.Inject

// TODO
class ImagesDao @Inject constructor(){
	fun load(pageNumber: Int, pageSize: Int): List<ImageEntity> {
		return listOf(ImageEntity("image A", ByteArray(1)), ImageEntity("image B", ByteArray(2)))
	}
}

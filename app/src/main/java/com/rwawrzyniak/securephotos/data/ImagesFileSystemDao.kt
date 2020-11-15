package com.rwawrzyniak.securephotos.data

import com.rwawrzyniak.securephotos.core.android.DataState
import com.rwawrzyniak.securephotos.data.DataConstants.THUMBNAIL
import com.rwawrzyniak.securephotos.storage.FileImageProvider
import java.io.File
import javax.inject.Inject

class ImagesFileSystemDao @Inject constructor(private val fileImageProvider: FileImageProvider) {
	fun load(pageNumber: Int, pageSize: Int): DataState<List<File>> =
		fileImageProvider.readFilesPaged(
			pageNumber, pageSize,
			THUMBNAIL
		)

	fun save(name: String, readBytes: ByteArray) = fileImageProvider.save(name, readBytes)
}

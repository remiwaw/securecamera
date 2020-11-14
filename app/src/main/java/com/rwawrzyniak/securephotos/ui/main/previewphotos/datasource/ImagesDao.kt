package com.rwawrzyniak.securephotos.ui.main.previewphotos.datasource

import com.rwawrzyniak.securephotos.core.android.Constants
import com.rwawrzyniak.securephotos.core.android.DataState
import com.rwawrzyniak.securephotos.storage.FileImageProvider
import java.io.File
import javax.inject.Inject

class ImagesDao @Inject constructor(private val fileImageProvider: FileImageProvider){
	fun load(pageNumber: Int, pageSize: Int): DataState<List<File>> = fileImageProvider.readFilesPaged(pageNumber, pageSize, Constants.THUMBNAIL)
	fun save(name: String, readBytes: ByteArray) = fileImageProvider.save(name, readBytes)
}

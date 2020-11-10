package com.rwawrzyniak.securephotos.ui.main.previewphotos.datasource

import java.io.File
import javax.inject.Inject

// TODO encryption here?
class ImagesDao @Inject constructor(private val fileImageProvider: FileImageProvider){
	fun load(pageNumber: Int, pageSize: Int): DataState<List<File>> = fileImageProvider.readFilesPaged(pageNumber, pageSize)
	fun save(name: String, readBytes: ByteArray) = fileImageProvider.save(name, readBytes)
}

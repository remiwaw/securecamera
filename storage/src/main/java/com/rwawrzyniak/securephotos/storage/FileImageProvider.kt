package com.rwawrzyniak.securephotos.storage

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.rwawrzyniak.securephotos.core.android.DataState
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class FileImageProvider @Inject constructor(private val context: Context){

	// TODO Actually it'd be better to just give back as result a path to the file, not file itself / byte array
	// So we would avoid storing so much data in memory.
	fun readFilesPaged(pageNumber: Int, pageSize: Int, withPrefix: String = ""): DataState<List<File>> {
		return  try {
			val dir = getDir()

			if(!dir.exists()){
				return DataState.Success(listOf())
			}

			val allFiles = dir.listFiles().sortedByDescending { it.lastModified() }.filter { it.isFile && it.name.contains(withPrefix) }
			val startIndex = if(pageNumber == 1) {
				0
			} else {
				pageNumber*pageSize-pageSize
			}

			val endIndex = if(pageNumber*pageSize > allFiles.size) allFiles.size else pageNumber*pageSize
			val pagedList: List<File> = allFiles.subList(startIndex, endIndex)
            DataState.Success(pagedList)
		} catch (e: Exception){
            DataState.Error(e)
		}
	}

	// Not used yet, but it could be used to read encrypted image in full resolution.
	fun read(fileName: String): DataState<File> = try {
		val dir = getDir()
		val file = dir.listFiles().first { it.isFile && it.name == fileName }
		DataState.Success(file)
	} catch (e: Exception){
		DataState.Error(e)
	}

	fun save(fileName: String, byteArray: ByteArray): DataState<File> {
		return try {
			val dir = getDir()
			dir.mkdirs()

			val file = File(dir, fileName)
			file.createNewFile()

			FileOutputStream(file)
				.use { it.write(byteArray) }

			DataState.Success(file)
		} catch (e: Exception){
			DataState.Error(e)
		}
	}

	private fun getDir(): File {
		// TODO now we are saving data only to internal storage, it has follwing advantages:
		// 1) Doesn't require additional permission
		// 2) In Android version Q Environment.getExternalStorageDirectory() is deprecated, we would have to use new API.
		// 3) Data is deleted when app is unistalled which could be a pro or con
		// 4) App itself takes more place if we add more photos.
		val root = context.filesDir
		return File("${root.absolutePath}$FOLDER")
	}

	companion object {
		@VisibleForTesting const val FOLDER = "/encryptedPictures"
	}
}

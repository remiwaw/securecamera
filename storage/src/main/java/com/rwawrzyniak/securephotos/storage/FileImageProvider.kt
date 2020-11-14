package com.rwawrzyniak.securephotos.storage

import android.os.Environment
import com.rwawrzyniak.securephotos.core.android.DataState
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class FileImageProvider @Inject constructor(){

	fun readFilesPaged(pageNumber: Int, pageSize: Int): DataState<List<File>> {
		return  try {
			if(!isExternalStorageReadable()){
                DataState.Error(Exception("External storage not readable"))
			}

			val dir = getDir()

			if(!dir.exists()){
				return DataState.Success(listOf())
			}

			// TODO make sure there is no out of bound exception
			val allFiles = dir.listFiles().filter { it.isFile }
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

	fun save(fileName: String, byteArray: ByteArray): DataState<Unit> = try {
		if(!isExternalStorageWritable()){
            DataState.Error(Exception("External storage not writable"))
		}

		val dir = getDir()
		dir.mkdirs()

		val file = File(dir, fileName)
		file.createNewFile()

		val fos = FileOutputStream(file)
		fos.write(byteArray)
		fos.close()
        DataState.Success(Unit)
	} catch (e: Exception){
        DataState.Error(e)
	}

	private fun getDir(): File {
		// TODO remove deprecation... this may be problematic with target android 10
		val root = Environment.getExternalStorageDirectory()
		return File("${root.absolutePath}$FOLDER")
	}

	private fun isExternalStorageWritable(): Boolean {
		return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
	}

	private fun isExternalStorageReadable(): Boolean {
		return Environment.getExternalStorageState() in
				setOf(Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY)
	}

	companion object {
		private const val FOLDER = "/encryptedPictures"
	}
}

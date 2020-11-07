package com.rwawrzyniak.securephotos.ui.main.previewphotos.datasource

import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

// TOOD move to storage module
class FileImageProvider @Inject constructor(){

	fun readFilesPaged(pageNumber: Int, pageSize: Int): List<File> {
		val dir = getDir()

		if(!dir.exists()){
			return listOf()
		}

		// TODO make sure there is no out of bound exception
		val allFiles = dir.listFiles().filter { it.isFile }
		val startIndex = if(pageNumber == 1) {
			0
		} else {
			if((pageNumber-1*pageSize)-1 > allFiles.size-1) 0 else (pageNumber-1*pageSize)-1
		}

		val endIndex = if(pageNumber*pageSize > allFiles.size) allFiles.size else pageNumber*pageSize
		return allFiles.subList(startIndex, endIndex)
	}

	// TODO remove deprecation...
	fun save(fileName: String, byteArray: ByteArray){
		if(!isExternalStorageWritable()){
			Log.w("sss", "external storage not writable")

		}
		val dir = getDir()
		dir.mkdirs()

		val file = File(dir, fileName)
		file.createNewFile()

		val fos = FileOutputStream(file)
		fos.write(byteArray)
		fos.close()
	}

	private fun getDir(): File {
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

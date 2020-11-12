package com.rwawrzyniak.securephotos.ext

import java.io.File
import java.io.FileInputStream

fun File.toByteArray(): ByteArray {
	val inputStream = FileInputStream(this)
	val inputBytes = ByteArray(this.length().toInt())
	inputStream.read(inputBytes)
	inputStream.close()
	return inputBytes
}

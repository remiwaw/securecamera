package com.rwawrzyniak.securephotos.ui.main.previewphotos.datasource

import com.rwawrzyniak.securephotos.core.android.Constants.THUMBNAIL
import com.rwawrzyniak.securephotos.core.android.Constants.THUMBNAIL_WIDTH_HEIGHT
import com.rwawrzyniak.securephotos.core.android.DataState
import com.rwawrzyniak.securephotos.core.android.ResizeBitmapUseCase
import com.rwawrzyniak.securephotos.ext.toByteArray
import com.rwawrzyniak.securephotos.ui.main.encryption.usecase.EncryptDecryptDataUseCase
import com.rwawrzyniak.securephotos.ui.main.previewphotos.datasource.mapper.ByteArrayBitMapMapper
import java.io.File
import javax.inject.Inject

class  ImagesRepository @Inject constructor(
	private val imagesDao: ImagesDao,
	private val encryptDecryptDataUseCase: EncryptDecryptDataUseCase,
	private val bitmapUseCase: ResizeBitmapUseCase,
	private val byteArrayBitMapMapper: ByteArrayBitMapMapper
) {
	fun loadAndDecrypt(pageNumber: Int, pageSize: Int): DataState<List<ImageEntity>> {
		return when (val encryptedFiles: DataState<List<File>> = imagesDao.load(pageNumber, pageSize)) {
			is DataState.Success -> {
				try {
					DataState.Success(encryptedFiles.data.map { file -> ImageEntity(
						title = file.name,
						byteArray = encryptDecryptDataUseCase.decrypt(file)
					) })
				} catch (exception: Exception){
					DataState.Error(exception)
				}
			}
			is DataState.Error -> {
				DataState.Error(encryptedFiles.exception)
			}
			else -> {
				DataState.Error(IllegalArgumentException("Not expected data state"))
			}
		}
	}

	fun saveAndEncrypt(file: File){
		imagesDao.save(file.name, encryptDecryptDataUseCase.encrypt(file))
		val thumbnail = bitmapUseCase.resizeBitmap(byteArrayBitMapMapper.mapFromEntity(file.toByteArray()), THUMBNAIL_WIDTH_HEIGHT)
		val thumbnailByteArray = byteArrayBitMapMapper.mapToEntity(thumbnail)
		imagesDao.save("$THUMBNAIL${file.name}", encryptDecryptDataUseCase.encrypt(thumbnailByteArray))
	}
}

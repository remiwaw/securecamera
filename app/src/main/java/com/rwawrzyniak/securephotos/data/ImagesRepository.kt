package com.rwawrzyniak.securephotos.data

import com.rwawrzyniak.securephotos.core.android.Constants
import com.rwawrzyniak.securephotos.core.android.DataState
import com.rwawrzyniak.securephotos.core.android.ResizeBitmapUseCase
import com.rwawrzyniak.securephotos.core.android.ext.toByteArray
import com.rwawrzyniak.securephotos.data.DataConstants.THUMBNAIL
import com.rwawrzyniak.securephotos.data.DataConstants.THUMBNAIL_WIDTH_HEIGHT
import com.rwawrzyniak.securephotos.data.model.ImageEntity
import com.rwawrzyniak.securephotos.encryption.usecase.EncryptDecryptDataUseCase
import com.rwawrzyniak.securephotos.ui.main.previewphotos.datasource.mapper.ByteArrayBitMapMapper
import java.io.File
import javax.inject.Inject

class  ImagesRepository @Inject constructor(
	private val imagesFileSystemDao: ImagesFileSystemDao,
	private val encryptDecryptDataUseCase: EncryptDecryptDataUseCase,
	private val bitmapUseCase: ResizeBitmapUseCase,
	private val byteArrayBitMapMapper: ByteArrayBitMapMapper
) {
	fun loadAndDecrypt(pageNumber: Int, pageSize: Int): DataState<List<ImageEntity>> {
		return when (val encryptedFiles: DataState<List<File>> = imagesFileSystemDao.load(pageNumber, pageSize)) {
			is DataState.Success -> {
				try {
                    DataState.Success(encryptedFiles.data.map { file ->
                        ImageEntity(
                            title = file.name,
                            byteArray = encryptDecryptDataUseCase.decrypt(file)
                        )
                    })
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
		imagesFileSystemDao.save(file.name, encryptDecryptDataUseCase.encrypt(file))
		val thumbnail = bitmapUseCase.resizeBitmap(
			byteArrayBitMapMapper.mapFromEntity(file.toByteArray()),
			THUMBNAIL_WIDTH_HEIGHT
		)
		val thumbnailByteArray = byteArrayBitMapMapper.mapToEntity(thumbnail)
		imagesFileSystemDao.save("${THUMBNAIL}${file.name}", encryptDecryptDataUseCase.encrypt(thumbnailByteArray))
	}
}

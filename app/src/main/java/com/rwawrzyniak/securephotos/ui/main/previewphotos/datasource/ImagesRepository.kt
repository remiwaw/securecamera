package com.rwawrzyniak.securephotos.ui.main.previewphotos.datasource

import com.rwawrzyniak.securephotos.core.android.DataState
import com.rwawrzyniak.securephotos.ext.toByteArray
import com.rwawrzyniak.securephotos.ui.main.encryption.usecase.EncryptDecryptDataUseCase
import java.io.File
import javax.inject.Inject

class  ImagesRepository @Inject constructor(
	private val imagesDao: ImagesDao,
	private val encryptDecryptDataUseCase: EncryptDecryptDataUseCase
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
	}
}

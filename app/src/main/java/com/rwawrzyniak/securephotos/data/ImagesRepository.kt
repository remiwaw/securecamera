package com.rwawrzyniak.securephotos.data

import com.rwawrzyniak.securephotos.core.android.DataState
import com.rwawrzyniak.securephotos.core.android.ext.toByteArray
import com.rwawrzyniak.securephotos.data.DataConstants.THUMBNAIL
import com.rwawrzyniak.securephotos.data.DataConstants.THUMBNAIL_WIDTH_HEIGHT
import com.rwawrzyniak.securephotos.data.model.ImageEntity
import com.rwawrzyniak.securephotos.encryption.usecase.EncryptDecryptDataUseCase
import com.rwawrzyniak.securephotos.ui.main.previewphotos.datasource.mapper.ByteArrayBitMapMapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import me.echodev.resizer.Resizer
import java.io.File
import javax.inject.Inject

@ExperimentalCoroutinesApi
class ImagesRepository @Inject constructor(
	private val imagesFileSystemDao: ImagesFileSystemDao,
	private val encryptDecryptDataUseCase: EncryptDecryptDataUseCase,
	private val resizer: Resizer,
	private val byteArrayBitMapMapper: ByteArrayBitMapMapper
) {
	fun loadAndDecrypt(pageNumber: Int, pageSize: Int): DataState<List<ImageEntity>> {
		return when (val encryptedFiles: DataState<List<File>> =
			imagesFileSystemDao.load(pageNumber, pageSize)) {
			is DataState.Success -> {
				try {
					DataState.Success(encryptedFiles.data.map { file ->
						ImageEntity(
							title = file.name,
							byteArray = encryptDecryptDataUseCase.decrypt(file)
						)
					})
				} catch (exception: Exception) {
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

	// TODO This is not typial for repository, normally we should save/read only.
	// Saving 2 files should be separated to a new use case. Or rename repository
	fun saveAndEncryptOriginalAndThumbnail(file: File): DataState<File> {
		// We save file in two versions: full resolution and one only thumbnails
		return try {
			imagesFileSystemDao.save(file.name, encryptDecryptDataUseCase.encrypt(file))

			// Thumbnail
			val thumbnail = resizer.setTargetLength(THUMBNAIL_WIDTH_HEIGHT)
				.setSourceImage(file)
				.resizedBitmap

			val thumbnailByteArray = byteArrayBitMapMapper.mapToEntity(thumbnail)
			imagesFileSystemDao.save(
				"${THUMBNAIL}${file.name}",
				encryptDecryptDataUseCase.encrypt(thumbnailByteArray)
			)
		} finally {
			// Delete non encrypted file
			file.delete()
		}
	}
}

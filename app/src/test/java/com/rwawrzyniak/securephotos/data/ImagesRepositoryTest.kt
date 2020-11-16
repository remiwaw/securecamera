package com.rwawrzyniak.securephotos.data

import android.graphics.Bitmap
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import com.nhaarman.mockitokotlin2.*
import com.rwawrzyniak.securephotos.core.android.DataState
import com.rwawrzyniak.securephotos.core.android.ResizeBitmapUseCase
import com.rwawrzyniak.securephotos.data.model.ImageEntity
import com.rwawrzyniak.securephotos.encryption.usecase.EncryptDecryptDataUseCase
import com.rwawrzyniak.securephotos.ui.main.previewphotos.datasource.mapper.ByteArrayBitMapMapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.io.File
@ExperimentalCoroutinesApi
internal class ImagesRepositoryTest {

	@Mock
	private lateinit var imagesFileSystemDao: ImagesFileSystemDao
	@Mock
	private lateinit var encryptDecryptDataUseCase: EncryptDecryptDataUseCase
	@Mock
	private lateinit var bitmapUseCase: ResizeBitmapUseCase
	@Mock
	private lateinit var byteArrayBitMapMapper: ByteArrayBitMapMapper
	@Mock
	private lateinit var file1: File
	@Mock
	private lateinit var file2: File
	@Mock
	private lateinit var file3: File
	@Mock
	private lateinit var thumbnail: Bitmap

	@Before
	fun setUp() {
		MockitoAnnotations.openMocks(this)
	}

	@Test
	fun shouldMapExceptionIfErrorInDecryption(){
		val exception = RuntimeException("Terrible error!")

		val pageNumber = 1
		val pageSize = 3
		file1.stub { on { name } doReturn "file1name" }

		encryptDecryptDataUseCase.stub {
			on { decrypt(file1) } doThrow exception
		}

		imagesFileSystemDao.stub {
			on { load(pageNumber, pageSize) } doReturn DataState.Success(listOf(file1))
		}

		val sut = sut()

		val result = sut.loadAndDecrypt(pageNumber, pageSize)
		val actual = (result as DataState.Error)
		assertThat(actual.exception).isEqualTo(exception)

		verify(imagesFileSystemDao).load(1,3)
	}

	@Test
	fun shouldLoadAndDecrypt(){
		val byteArray1 = ByteArray(10)
		val byteArray2 = ByteArray(10)
		val byteArray3 = ByteArray(10)

		val pageNumber = 1
		val pageSize = 3
		file1.stub { on { name } doReturn "file1name" }
		file2.stub { on { name } doReturn "file2name" }
		file3.stub { on { name } doReturn "file3name" }

		encryptDecryptDataUseCase.stub {
			on { decrypt(file1) } doReturn byteArray1
			on { decrypt(file2) } doReturn byteArray2
			on { decrypt(file3) } doReturn byteArray3
		}

		imagesFileSystemDao.stub {
			on { load(pageNumber, pageSize) } doReturn DataState.Success(listOf(file1, file2, file3))
		}

		val sut = sut()

		val result = sut.loadAndDecrypt(pageNumber, pageSize)
		val actual = (result as DataState.Success).data
		assertThat(actual).containsExactly(
			ImageEntity(file1.name, byteArray1),
			ImageEntity(file2.name, byteArray2),
			ImageEntity(file3.name, byteArray3),
		)

		verify(imagesFileSystemDao).load(1,3)
	}

	@Test
    fun shouldSavePictureInTwoVersionsEncryptAndDeleteOriginal() {
		val encryptedByteArrayFullImage = ByteArray(10)
		val unencryptedArrayThumbnail = ByteArray(1)
		val encryptedByteArrayThumbnail = ByteArray(1)

		file1.stub {
			on { name } doReturn "testFile"
		}

		bitmapUseCase.stub {
			on { resizeBitmap(any(), any()) } doReturn thumbnail
		}

		byteArrayBitMapMapper.stub {
			on { mapToEntity(thumbnail) } doReturn unencryptedArrayThumbnail
			on { mapFromEntity(file1) } doReturn thumbnail
		}

		imagesFileSystemDao.stub {
			on { save(any(), any()) } doReturn DataState.Success(file1)
		}

		encryptDecryptDataUseCase.stub {
			on { encrypt(file1) } doReturn encryptedByteArrayFullImage
			on { encrypt(unencryptedArrayThumbnail) } doReturn encryptedByteArrayThumbnail
		}

		val sut = sut()

		sut.saveAndEncryptOriginalAndThumbnail(file1)

		verify(imagesFileSystemDao).save("testFile", encryptedByteArrayFullImage)
		verify(imagesFileSystemDao).save("${DataConstants.THUMBNAIL}testFile", encryptedByteArrayThumbnail)

		verify(file1).delete()
    }

	private fun sut() = ImagesRepository(
		imagesFileSystemDao,
		encryptDecryptDataUseCase,
		bitmapUseCase,
		byteArrayBitMapMapper
	)
}

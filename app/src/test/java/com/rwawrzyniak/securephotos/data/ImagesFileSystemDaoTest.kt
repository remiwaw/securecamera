package com.rwawrzyniak.securephotos.data

import com.nhaarman.mockitokotlin2.verify
import com.rwawrzyniak.securephotos.data.DataConstants.THUMBNAIL
import com.rwawrzyniak.securephotos.storage.FileImageProvider
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

internal class ImagesFileSystemDaoTest {

	@Mock
	private lateinit var fileImageProvider: FileImageProvider

	@Before
	fun setUp() {
		MockitoAnnotations.openMocks(this)
	}

	@Test
	fun shouldUseFileImageProviderForLoading(){
		val pageNumber = 1
		val pageSize = 5
		sut().load(pageNumber, pageSize)
		verify(fileImageProvider).readFilesPaged(pageNumber, pageSize, THUMBNAIL)
	}

	@Test
	fun shouldUseFileImageProviderForSaving(){
		val title = "exampleTitle"
		val byteArrayToSave = ByteArray(10)
		sut().save(title, byteArrayToSave)
		verify(fileImageProvider).save(title, byteArrayToSave)
	}

	private fun sut() = ImagesFileSystemDao(fileImageProvider)
}

package com.rwawrzyniak.securephotos.storage

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import assertk.assertions.isEqualToWithGivenProperties
import com.rwawrzyniak.securephotos.core.android.DataState
import com.rwawrzyniak.securephotos.storage.FileImageProvider.Companion.FOLDER
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import java.io.File
import java.lang.Exception

@RunWith(RobolectricTestRunner::class)
internal class FileImageProviderTest {

	private val context: Context = ApplicationProvider.getApplicationContext();

	val rootFolder = File(context.filesDir.absolutePath + FOLDER)

	@Before
	fun setUp() {
		MockitoAnnotations.initMocks(this)
	}

	@Test
	fun shouldReturnEmptyListIfNoFiles(){
		val sut = sut()
		val result = sut.readFilesPaged(1,3)
		assertk.assertThat((result as DataState.Success).data).isEqualTo(listOf())
	}

	@Test
	fun shouldReadFilesPaged() {
		val sut = sut()
		val pageNumber = 1
		val pageSize = 3

		// Save some files first
		sut.save("file1", ByteArray(10))
		sut.save("file2", ByteArray(11))
		sut.save("file3", ByteArray(12))
		sut.save("file4", ByteArray(13))
		sut.save("file5", ByteArray(14))
		sut.save("file6", ByteArray(15))

		val page = sut.readFilesPaged(pageNumber, pageSize)
		val data = (page as DataState.Success<List<File>>).data

		// file 6 is the newest then 5 then 4
		// TODO roboletric doesnt specify lastModifed date, so its impossible to check if we get
		// correct items
//		assertThat(data.map{it.name}).containsExactly("file6", "file5", "file4")
	}

	@Test
	fun shouldSaveAndRead() {
		val expectedFile = File(rootFolder, "testImage")
		val testByteArray = ByteArray(10)

		val sut = sut()

		val saveResult = sut.save(expectedFile.name, testByteArray)
		assertThat(saveResult).isEqualTo(DataState.Success(expectedFile))

		val readResult = sut.read(expectedFile.name)
		assertk.assertThat(readResult).isEqualTo(DataState.Success(expectedFile))
	}

	@Test
	fun shouldReturnDataStateFailIfNoSuchFile() {
		val expectedFile = File(rootFolder, "testImage")
		val nonExistingFileName = "NonExistingFileName"
		val testByteArray = ByteArray(10)

		val sut = sut()

		val saveResult = sut.save(expectedFile.name, testByteArray)
		assertThat(saveResult).isEqualTo(DataState.Success(expectedFile))

		val readResult = sut.read(nonExistingFileName)
		assertThat((readResult as DataState.Error).exception.message).isEqualTo("Array contains no element matching the predicate.")
	}

	private fun sut() = FileImageProvider(context)
}

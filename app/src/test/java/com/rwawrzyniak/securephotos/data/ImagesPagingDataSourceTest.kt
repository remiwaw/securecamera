package com.rwawrzyniak.securephotos.data

import android.graphics.Bitmap
import androidx.paging.PagingSource
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.stub
import com.rwawrzyniak.securephotos.core.android.DataState
import com.rwawrzyniak.securephotos.data.model.ImageEntity
import com.rwawrzyniak.securephotos.ui.main.previewphotos.datasource.mapper.ImageMapper
import com.rwawrzyniak.securephotos.ui.main.previewphotos.datasource.mapper.ImageModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
@ExperimentalCoroutinesApi
internal class ImagesPagingDataSourceTest {

	@Mock
	private lateinit var imagesRepository: ImagesRepository
	@Mock
	private lateinit var imageMapper: ImageMapper
	@Mock
	private lateinit var bitmap: Bitmap


	@Before
	fun setUp() {
		MockitoAnnotations.openMocks(this)
	}

	@Test
	fun shouldMapExceptionWhenThrown() {
		val pageSize = 3
		val pageNumber = 1

		val initialParams = PagingSource.LoadParams.Refresh(
			key = pageNumber,
			loadSize = pageSize,
			placeholdersEnabled = true
		)

		val expectedException = Exception("Some terrible exception")

		imagesRepository.stub {
			on { loadAndDecrypt(any(), any()) } doReturn DataState.Error(expectedException)
		}

		val sut = sut()

		runBlockingTest {
			val result: PagingSource.LoadResult<Int, ImageModel> = sut.load(initialParams)
			assertThat((result as PagingSource.LoadResult.Error).throwable).isEqualTo (expectedException)
		}
	}


	@Test
    fun shouldLoadFirstPageWithGivenSizeAndGiveBackNextKey() {

		val imageEntity1 = ImageEntity("1", ByteArray(10))
		val imageEntity2 = ImageEntity("2", ByteArray(10))
		val imageEntity3 = ImageEntity("3", ByteArray(10))
		val imageEntity4 = ImageEntity("4", ByteArray(10))
		val imageEntity5 = ImageEntity("5", ByteArray(10))

		val imageModel1 = ImageModel("1", bitmap)
		val imageModel2 = ImageModel("2", bitmap)
		val imageModel3 = ImageModel("3", bitmap)
		val imageModel4 = ImageModel("4", bitmap)
		val imageModel5 = ImageModel("5", bitmap)


		val pageSize = 3
		val pageNumber = 1

		val initialParams = PagingSource.LoadParams.Refresh(
			key = pageNumber,
			loadSize = pageSize,
			placeholdersEnabled = true
		)

		imagesRepository.stub {
			on { loadAndDecrypt(pageNumber, pageSize) } doReturn DataState.Success(listOf(
				imageEntity1,
				imageEntity2,
				imageEntity3
			))
		}

		imageMapper.stub {
			on { mapFromEntity(imageEntity1) } doReturn imageModel1
			on { mapFromEntity(imageEntity2) } doReturn imageModel2
			on { mapFromEntity(imageEntity3) } doReturn imageModel3
			on { mapFromEntity(imageEntity4) } doReturn imageModel4
			on { mapFromEntity(imageEntity5) } doReturn imageModel5

			on { mapToEntity(imageModel1) } doReturn imageEntity1
			on { mapToEntity(imageModel2) } doReturn imageEntity2
			on { mapToEntity(imageModel3) } doReturn imageEntity3
			on { mapToEntity(imageModel4) } doReturn imageEntity4
			on { mapToEntity(imageModel5) } doReturn imageEntity5
		}

		val sut = sut()

		runBlockingTest {
			val result: PagingSource.LoadResult<Int, ImageModel> = sut.load(initialParams)
			assertThat((result as PagingSource.LoadResult.Page).data).containsExactly (
				imageModel1,
				imageModel2,
				imageModel3
			)
			assertThat(result.nextKey).isEqualTo(2)
		}

    }

	private fun sut() = ImagesPagingDataSource(imagesRepository, imageMapper)
}

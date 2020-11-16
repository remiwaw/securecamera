package com.rwawrzyniak.securephotos.data

import androidx.paging.PagingSource
import com.rwawrzyniak.securephotos.core.android.DataState
import com.rwawrzyniak.securephotos.data.model.ImageEntity
import com.rwawrzyniak.securephotos.ui.main.previewphotos.datasource.mapper.ImageModel
import com.rwawrzyniak.securephotos.ui.main.previewphotos.datasource.mapper.ImageMapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
class ImagesPagingDataSource @Inject constructor(
	private val imagesRepository: ImagesRepository,
	private val imageMapper: ImageMapper
) : PagingSource<Int, ImageModel>() {

	override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ImageModel> {
		val pageNumber = params.key ?: INITIAL_PAGE
		val pageSize = params.loadSize
		val dataState: DataState<List<ImageEntity>> =
			imagesRepository.loadAndDecrypt(pageNumber, pageSize)
		return when (dataState) {
			is DataState.Success -> {
				val imageEntities =
					dataState.data.map { imageEntity -> imageMapper.mapFromEntity(imageEntity) }
				mapToLoadResult(imageEntities, params)
			}
			is DataState.Error -> LoadResult.Error(dataState.exception)
			else -> throw IllegalArgumentException("Not supported type")
		}
	}

	private fun mapToLoadResult(
		result: List<ImageModel>,
		loadParams: LoadParams<Int>
	): LoadResult<Int, ImageModel> {
		val currentPageNumber = loadParams.key ?: INITIAL_PAGE
		val prevKey = (currentPageNumber - 1).let { if (it < INITIAL_PAGE) null else it }
		val nextKey =
			(currentPageNumber + 1).let { if (result.size < loadParams.loadSize) null else it }

		return LoadResult.Page(result, prevKey, nextKey)
	}

	companion object {
		const val INITIAL_PAGE = 1
	}
}

package com.rwawrzyniak.securephotos.ui.main.previewphotos.datasource

import androidx.paging.PagingSource
import com.rwawrzyniak.securephotos.core.android.DataState
import com.rwawrzyniak.securephotos.ui.main.previewphotos.datasource.mapper.ImageDto
import com.rwawrzyniak.securephotos.core.android.DataState.Error
import com.rwawrzyniak.securephotos.ui.main.previewphotos.datasource.mapper.ImageMapper
import java.io.File
import javax.inject.Inject

class ImagesPagingDataSource @Inject constructor(
    private val imagesRepository: ImagesRepository,
	private val imageMapper: ImageMapper
) : PagingSource<Int, ImageDto>() {

	override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ImageDto> {
		val pageNumber = params.key ?: INITIAL_PAGE
		val pageSize = params.loadSize
		val dataState: DataState<List<ImageEntity>> = imagesRepository.loadAndDecrypt(pageNumber, pageSize)
		return when (dataState) {
			is DataState.Success -> {
				val imageEntities = dataState.data.map { imageEntity -> imageMapper.mapFromEntity(imageEntity) }
				mapToLoadResult(imageEntities, params)
			}
			is Error -> LoadResult.Error(dataState.exception)
			else -> throw error("Not supported type")
		}
	}

    private fun mapToLoadResult(
		result: List<ImageDto>,
		loadParams: LoadParams<Int>
    ): LoadResult<Int, ImageDto> {
        val currentPageNumber = loadParams.key ?: INITIAL_PAGE
        val prevKey = (currentPageNumber - 1).let { if (it < INITIAL_PAGE) null else it }
        val nextKey = (currentPageNumber + 1).let { if (result.size < loadParams.loadSize) null else it }

        return LoadResult.Page(result, prevKey, nextKey)
    }

    companion object {
        const val INITIAL_PAGE = 1
    }
}

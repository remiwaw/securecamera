package com.rwawrzyniak.securephotos.ui.main.previewphotos.datasource

import androidx.paging.PagingSource
import com.rwawrzyniak.securephotos.ui.main.previewphotos.ImageDto
import com.rwawrzyniak.securephotos.ui.main.previewphotos.datasource.mapper.ImageMapper
import javax.inject.Inject

// TODO
class ImagesDataSource @Inject constructor(
    private val imagesDao: ImagesDao,
	private val imageMapper: ImageMapper
) : PagingSource<Int, ImageDto>() {

	override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ImageDto> {
		val pageNumber = params.key ?: INITIAL_PAGE
		val pageSize = params.loadSize
		val imageEntities: List<ImageDto> = imagesDao.load(pageNumber, pageSize).map {
			imageMapper.mapFromEntity(
				ImageEntity(it.name, it.readBytes())
			)
		}
		return mapToLoadResult(imageEntities, params)
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

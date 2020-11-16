package com.rwawrzyniak.securephotos.ui.main.previewphotos.ui

import androidx.paging.*
import com.rwawrzyniak.securephotos.ui.main.previewphotos.datasource.mapper.ImageModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// class created to make test easier
class CreatePagerUseCase @Inject constructor() {
	fun createFlow(
		viewModelScope: CoroutineScope,
		config: PagingConfig,
		pagingSourceFactory: () -> PagingSource<Int, ImageModel>,
		initialKey: Int = 1,
		): Flow<PagingData<ImageModel>> =
		Pager(config, initialKey = initialKey, pagingSourceFactory = pagingSourceFactory)
			.flow
			.cachedIn(viewModelScope)

}

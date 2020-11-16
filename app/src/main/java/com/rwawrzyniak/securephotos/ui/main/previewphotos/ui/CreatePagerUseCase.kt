package com.rwawrzyniak.securephotos.ui.main.previewphotos.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.rwawrzyniak.securephotos.ui.main.previewphotos.datasource.mapper.ImageDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// class created to make test easier
class CreatePagerUseCase @Inject constructor() {
	fun createFlow(
		viewModelScope: CoroutineScope,
		config: PagingConfig,
		pagingSourceFactory: () -> PagingSource<Int, ImageDto>,
		initialKey: Int = 1,
		): Flow<PagingData<ImageDto>> =
		Pager(config, initialKey = initialKey, pagingSourceFactory = pagingSourceFactory)
			.flow
			.cachedIn(viewModelScope)

}

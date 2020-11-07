package com.rwawrzyniak.securephotos.ui.main.previewphotos

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rwawrzyniak.securephotos.ui.main.previewphotos.datasource.ImagesDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

internal abstract class PreviewPhotosViewModel : ViewModel() {
	abstract fun observeState(): Flow<PreviewPhotosViewState>
	abstract fun observeEffect(): Flow<PreviewPhotosViewEffect>
	abstract suspend fun onAction(action: PreviewPhotosViewAction)
}


@ExperimentalCoroutinesApi
internal class PreviewPhotosViewModelImpl @ViewModelInject constructor(
	@Assisted private val savedStateHandle: SavedStateHandle,
	private val imagesDataSource: ImagesDataSource
) : PreviewPhotosViewModel() {

	private val state = ConflatedBroadcastChannel<PreviewPhotosViewState>(PreviewPhotosViewState.Initialising)

	private val effects = BroadcastChannel<PreviewPhotosViewEffect>(1)

	override fun observeState(): Flow<PreviewPhotosViewState> = state.asFlow()

	override fun observeEffect(): Flow<PreviewPhotosViewEffect> = effects.asFlow()

	override suspend fun onAction(action: PreviewPhotosViewAction) {
		when(action){
			PreviewPhotosViewAction.Initialize -> onInitialize()
			is PreviewPhotosViewAction.OnLoadingFinished -> onLoadingFinished(action.itemCount)
		}
	}

	private suspend fun onInitialize() {
		effects.send(PreviewPhotosViewEffect.ShowLoadingIndicator)
		wirePagedList()
			.apply {
				updatePageList(pagingDataFlow = this)
			}
		effects.send(PreviewPhotosViewEffect.HideLoadingIndicator)
	}

	// TODO
	private suspend fun onLoadingFinished(itemCount: Int) {
		effects.send(PreviewPhotosViewEffect.HideLoadingIndicator)
		if (itemCount == 0) {
			effects.send(PreviewPhotosViewEffect.ShowEmptyList)
		}
	}

	private fun wirePagedList(): Flow<PagingData<ImageDto>> {
		val config = PagingConfig(
			pageSize = PAGE_SIZE,
			initialLoadSize = PAGE_SIZE,
			prefetchDistance = PREFETCH_DISTANCE
		)

		return Pager(config, initialKey = 1, pagingSourceFactory = { imagesDataSource })
			.flow
			.cachedIn(viewModelScope)
	}

	private suspend fun updatePageList(pagingDataFlow: Flow<PagingData<ImageDto>>) {
		when (val oldState = state.value) {
			is PreviewPhotosViewState.Initialising -> {
				state.send(PreviewPhotosViewState.ShowImages(header = "testHeader", pagingDataFlow = pagingDataFlow))
			}
			is PreviewPhotosViewState.ShowImages -> {
				state.send(oldState.copy(pagingDataFlow = pagingDataFlow))
			}
			else -> {
				throw IllegalArgumentException("No such state")
			}
		}
	}

	internal companion object {
		const val PAGE_SIZE = 30
		const val PREFETCH_DISTANCE = 10
	}
}

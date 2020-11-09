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
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

internal abstract class PreviewPhotosViewModel : ViewModel() {

	internal sealed class PreviewPhotosViewAction {
		data class OnLoadingFinished(val itemCount: Int) : PreviewPhotosViewAction()
		object Initialize : PreviewPhotosViewAction()
	}

	internal data class PreviewPhotosViewState(
		val pagingDataFlow: Flow<PagingData<ImageDto>>? = null,
		val isLoading: Boolean = true,
		val error: Throwable? = null
	)

	abstract fun observeState(): Flow<PreviewPhotosViewState>
	abstract fun onAction(action: PreviewPhotosViewAction)
}


@ExperimentalCoroutinesApi
internal class PreviewPhotosViewModelImpl @ViewModelInject constructor(
	@Assisted private val savedStateHandle: SavedStateHandle,
	private val imagesDataSource: ImagesDataSource
) : PreviewPhotosViewModel() {

	private val actionChannel = Channel<PreviewPhotosViewAction>(Channel.UNLIMITED)
	private val _state = MutableStateFlow(PreviewPhotosViewState())
	private val state: StateFlow<PreviewPhotosViewState>
		get() = _state

	override fun observeState(): Flow<PreviewPhotosViewState> = state

	override fun onAction(action: PreviewPhotosViewAction) {
		actionChannel.offer(action)
	}

	init {
		viewModelScope.launch {
			handleIntents()
		}
		onAction(PreviewPhotosViewAction.Initialize)
	}

	private suspend fun handleIntents() {
		actionChannel.consumeAsFlow().collect { action ->
			when (action) {
				is PreviewPhotosViewAction.OnLoadingFinished -> {
					if (action.itemCount == 0) {
						// show empty list text?
						{} //not items TODO
					}
				}
				PreviewPhotosViewAction.Initialize -> {
					_state.value = onInitialize()
				}
			}
		}
	}

	private fun onInitialize() = updatePageList(wirePagedList())

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

	private fun updatePageList(pagingDataFlow: Flow<PagingData<ImageDto>>) =
		PreviewPhotosViewState(pagingDataFlow = pagingDataFlow, isLoading = true, error = null)

	internal companion object {
		const val PAGE_SIZE = 30
		const val PREFETCH_DISTANCE = 10
	}
}

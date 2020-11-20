package com.rwawrzyniak.securephotos.ui.main.previewphotos.ui

import android.content.res.Resources
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.rwawrzyniak.securephotos.R
import com.rwawrzyniak.securephotos.data.ImagesPagingDataSource
import com.rwawrzyniak.securephotos.ui.main.previewphotos.datasource.mapper.ImageModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

internal abstract class PreviewPhotosViewModel : ViewModel() {

	internal sealed class PreviewPhotosViewAction {
		data class OnLoadingFailed(val error: Throwable) : PreviewPhotosViewAction()
		data class OnLoadingFinished(val itemCount: Int) : PreviewPhotosViewAction()
		object Initialize : PreviewPhotosViewAction()
	}

	internal data class PreviewPhotosViewState(
		val pagingDataFlow: Flow<PagingData<ImageModel>>? = null,
		val noPhotosAvailable: Boolean = true,
		val loadedImagesCountText: String = ""
	)

	internal sealed class PreviewPhotosViewEffect {
		data class ShowToastError(val errorMessage: String) : PreviewPhotosViewEffect()
	}

	abstract fun observeState(): Flow<PreviewPhotosViewState>
	abstract fun onAction(action: PreviewPhotosViewAction)
	abstract fun observeEffect(): SharedFlow<PreviewPhotosViewEffect>
}


@ExperimentalCoroutinesApi
internal class PreviewPhotosViewModelImpl @ViewModelInject constructor(
	private val imagesPagingDataSource: ImagesPagingDataSource,
	private val resources: Resources,
	private val createPagerUseCase: CreatePagerUseCase
) : PreviewPhotosViewModel() {

	private val _actionChannel = MutableSharedFlow<PreviewPhotosViewAction>()
	private val _state = MutableStateFlow(PreviewPhotosViewState())

	private val _effects = MutableSharedFlow<PreviewPhotosViewEffect>()

	override fun observeEffect(): SharedFlow<PreviewPhotosViewEffect> = _effects.asSharedFlow()

	override fun observeState(): StateFlow<PreviewPhotosViewState> = _state

	override fun onAction(action: PreviewPhotosViewAction) {
		viewModelScope.launch {
			_actionChannel.emit(action)
		}
	}

	init {
		viewModelScope.launch {
			handleActions()
		}
		onAction(PreviewPhotosViewAction.Initialize)
	}

	private suspend fun handleActions() {
		_actionChannel.asSharedFlow().collect { action ->
			when (action) {
				is PreviewPhotosViewAction.OnLoadingFinished -> {
					if (action.itemCount > 0) {
						_state.value = prepareLisLoadingCompleteState(action.itemCount)
					}
				}
				PreviewPhotosViewAction.Initialize -> _state.value = onInitialize()
				is PreviewPhotosViewAction.OnLoadingFailed -> onLoadingFailed(action.error)
			}
		}
	}

	private suspend fun onLoadingFailed(error: Throwable) {
		val errorMessage = error.message ?: resources.getString(R.string.error_loading_images)
		_effects.emit(PreviewPhotosViewEffect.ShowToastError(errorMessage))
	}

	private fun prepareLisLoadingCompleteState(itemCount: Int): PreviewPhotosViewState =
		_state.value.copy(
			noPhotosAvailable = false,
			loadedImagesCountText = resources.getString(R.string.item_count_header, itemCount)
		)

	private fun onInitialize() = updatePageList(wirePagedList())

	private fun wirePagedList(): Flow<PagingData<ImageModel>> {
		val config = PagingConfig(
			pageSize = PAGE_SIZE,
			initialLoadSize = PAGE_SIZE,
			prefetchDistance = PREFETCH_DISTANCE,
			enablePlaceholders = true
		)

		return createPagerUseCase.createFlow(viewModelScope, config, { imagesPagingDataSource })
	}

	private fun updatePageList(pagingDataFlow: Flow<PagingData<ImageModel>>) =
		PreviewPhotosViewState(pagingDataFlow = pagingDataFlow)

	internal companion object {
		const val PAGE_SIZE = 1
		const val PREFETCH_DISTANCE = 1
	}
}

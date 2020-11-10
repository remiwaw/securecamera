package com.rwawrzyniak.securephotos.ui.main.takepicture.ui

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rwawrzyniak.securephotos.ui.main.previewphotos.ui.PreviewPhotosViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

internal abstract class TakePictureViewModel : ViewModel() {

	internal sealed class TakePhotosViewAction {
		object TakePhoto : TakePhotosViewAction()
		object Initialize : TakePhotosViewAction()
	}

	internal data class TakePictureViewState(
		val isTakingPicture: Boolean = false,
		val isCheckPermission: Boolean = true
	)

	abstract fun observeState(): Flow<TakePictureViewState>
	abstract fun onAction(action: TakePhotosViewAction)
}

@ExperimentalCoroutinesApi
internal class TakePictureViewModelImpl @ViewModelInject constructor(
	@Assisted private val savedStateHandle: SavedStateHandle
	) : TakePictureViewModel() {

	private val actionChannel = Channel<TakePhotosViewAction>(Channel.UNLIMITED)
	private val _state = MutableStateFlow(TakePictureViewState())
	private val state: StateFlow<TakePictureViewState>
		get() = _state

	override fun observeState(): Flow<TakePictureViewState> = state
	override fun onAction(action: TakePhotosViewAction){
		actionChannel.offer(action)
	}

	init {
		viewModelScope.launch {
			handleActions()
		}
		onAction(TakePhotosViewAction.Initialize)
	}

	private suspend fun handleActions() {
		actionChannel.consumeAsFlow().collect { action ->
			when (action) {
				TakePhotosViewAction.TakePhoto -> _state.value = TakePictureViewState(isTakingPicture = true, isCheckPermission = false)
				TakePhotosViewAction.Initialize -> _state.value = TakePictureViewState(isTakingPicture = false, isCheckPermission = true)
			}
		}
	}
}

package com.rwawrzyniak.securephotos.ui.main.takepicture.ui

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rwawrzyniak.securephotos.ui.main.takepicture.ui.TakePictureViewModel.TakePictureViewEffect.StartCameraPreview
import com.rwawrzyniak.securephotos.ui.main.takepicture.ui.TakePictureViewModel.TakePictureViewEffect.TakePicture
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

internal abstract class TakePictureViewModel : ViewModel() {

	internal sealed class TakePhotosViewAction {
		object TakePictureButtonClicked : TakePhotosViewAction()
		object Initialize : TakePhotosViewAction()
	}

	internal sealed class TakePictureViewEffect{
		object TakePicture : TakePictureViewEffect()
		object StartCameraPreview : TakePictureViewEffect()
	}

	abstract fun onAction(action: TakePhotosViewAction)
	abstract fun observeEffect(): Flow<TakePictureViewEffect>
}

@ExperimentalCoroutinesApi
internal class TakePictureViewModelImpl @ViewModelInject constructor(
	@Assisted private val savedStateHandle: SavedStateHandle
	) : TakePictureViewModel() {

	private val _actionChannel = MutableSharedFlow<TakePhotosViewAction>()
	private val _effects = MutableSharedFlow<TakePictureViewEffect>()

	override fun observeEffect(): SharedFlow<TakePictureViewEffect> = _effects

	override fun onAction(action: TakePhotosViewAction){
		// this run on Main, because in the case of configuration change (i.e orientation change)
		// previewView passed to UseCameraUseCase become null. And we need to wait for its creaton (so on Main thread)
		viewModelScope.launch(Dispatchers.Main) {
			_actionChannel.emit(action)
		}
	}

	init {
		viewModelScope.launch(Dispatchers.IO) {
			handleActions()
		}
	}

	private suspend fun handleActions() {
		_actionChannel.asSharedFlow().collectLatest { action ->
			when (action) {
				TakePhotosViewAction.TakePictureButtonClicked -> _effects.emit(TakePicture)
				TakePhotosViewAction.Initialize -> _effects.emit(StartCameraPreview)
			}
		}
	}
}

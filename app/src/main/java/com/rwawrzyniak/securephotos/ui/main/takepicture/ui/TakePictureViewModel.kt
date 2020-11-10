package com.rwawrzyniak.securephotos.ui.main.takepicture.ui

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

internal abstract class TakePictureViewModel : ViewModel() {
	abstract fun observeState(): Flow<Boolean>
	abstract fun observeEffect(): Flow<TakePictureViewEffect>
	abstract suspend fun onAction(action: TakePictureViewAction)
}

@ExperimentalCoroutinesApi
internal class TakePictureViewModelImpl @ViewModelInject constructor(
	@Assisted private val savedStateHandle: SavedStateHandle
	) : TakePictureViewModel() {

	val state = ConflatedBroadcastChannel<Boolean>()
	val effects = BroadcastChannel<TakePictureViewEffect>(1)

	override fun observeState(): Flow<Boolean> = state.asFlow()

	override fun observeEffect(): Flow<TakePictureViewEffect> = effects.asFlow()

	override suspend fun onAction(action: TakePictureViewAction) {
		when(action){
			TakePictureViewAction.TakePhoto -> effects.send(TakePictureViewEffect.TakePicture)
		}
	}

}

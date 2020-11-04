package com.rwawrzyniak.securephotos.ui.main

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

internal abstract class TakePictureViewModel : ViewModel() {
	abstract fun observeState(): Flow<Boolean>
	abstract fun observeEffect(): Flow<Boolean>
	abstract fun onAction(action: TakePictureViewAction)
}


internal class TakePictureViewModelImpl : TakePictureViewModel() {

	val state = ConflatedBroadcastChannel<Boolean>()
	val effects = BroadcastChannel<Boolean>(1)

	override fun observeState(): Flow<Boolean> = state.asFlow()

	override fun observeEffect(): Flow<Boolean> = effects.asFlow()

	override fun onAction(action: TakePictureViewAction) {
		when(action){
			TakePictureViewAction.TakePhoto -> TODO()
		}
	}

}

package com.rwawrzyniak.securephotos.ui.main.appcode.ui

import android.content.res.Resources
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rwawrzyniak.securephotos.R
import com.rwawrzyniak.securephotos.ui.main.appcode.usecase.AppCodeCheckUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

internal abstract class AppCodeViewModel : ViewModel() {

	internal sealed class AppCodeViewAction {
		class SubmitButtonClicked(val appCode: String) : AppCodeViewAction()
	}

	internal sealed class AppCodeViewEffect {
		object NavigateToPreviousScreen : AppCodeViewEffect()
	}

	internal data class AppCodeViewState(
		val errorText: String = ""
	)

	abstract fun onAction(action: AppCodeViewAction)
	abstract fun observeEffect(): Flow<AppCodeViewEffect>
	abstract fun observeState(): Flow<AppCodeViewState>
}

@ExperimentalCoroutinesApi
internal class AppCodeViewModelImpl @ViewModelInject constructor(
	private val useCase: AppCodeCheckUseCase,
	private val resources: Resources
) : AppCodeViewModel() {

	private val _actionChannel = MutableSharedFlow<AppCodeViewAction>()
	private val _state = MutableStateFlow(AppCodeViewState())
	private val state: StateFlow<AppCodeViewState>
		get() = _state

	private val _effects = MutableSharedFlow<AppCodeViewEffect>()


	override fun observeState(): Flow<AppCodeViewState> = state

	override fun onAction(action: AppCodeViewAction) {
		viewModelScope.launch(Dispatchers.Main) {
			_actionChannel.emit(action)
		}
	}

	override fun observeEffect(): SharedFlow<AppCodeViewEffect> = _effects.asSharedFlow()

	init {
		viewModelScope.launch {
			handleActions()
		}
	}

	private suspend fun handleActions() {
		_actionChannel.asSharedFlow().collect { action ->
			when (action) {
				is AppCodeViewAction.SubmitButtonClicked -> onActionSubmitButtonClicked(action.appCode)
			}
		}
	}

	private suspend fun onActionSubmitButtonClicked(appCode: String) {
		if (useCase.isCorrectAppCode(appCode)) {
			_state.value = AppCodeViewState()
			_effects.emit(AppCodeViewEffect.NavigateToPreviousScreen)
		} else {
			val errorMessage = resources.getString(R.string.incorrectPasswordMessage)
			_state.value = AppCodeViewState(errorMessage)
		}
	}

}

package com.rwawrzyniak.securephotos.ui.main.appcode

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rwawrzyniak.securephotos.R
import com.rwawrzyniak.securephotos.core.android.BasicFragment
import com.rwawrzyniak.securephotos.ui.main.appcode.AppCodeViewModel.AppCodeViewAction.SubmitButtonClicked
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_app_code.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class AppCodeFragment : BasicFragment(R.layout.fragment_app_code) {
	private val viewModel: AppCodeViewModelImpl by viewModels()

	override fun onResume() {
		super.onResume()
		hideBackButton()
	}

	private fun hideBackButton() {
		(requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		setupUI()
		wireUpViewModel()
	}

	private fun setupUI() {
		submit.setOnClickListener {
			viewModel.onAction(SubmitButtonClicked(inputText.text.toString()))
		}
	}

	private fun wireUpViewModel() {
		viewModel.observeState()
			.onEach { state -> handleStateChanges(state) }
			.launchIn(lifecycleScope)

		viewModel.observeEffect()
			.onEach { effect -> handleEffect(effect) }
			.launchIn(lifecycleScope)
	}

	private fun handleStateChanges(state: AppCodeViewModel.AppCodeViewState) {
		if (state.errorText.isNotEmpty()) {
			appCodeInputLayout.error = state.errorText
		} else {
			appCodeInputLayout.error = null
		}
	}

	private fun handleEffect(effect: AppCodeViewModel.AppCodeViewEffect) {
		when(effect){
			AppCodeViewModel.AppCodeViewEffect.NavigateToPreviousScreen -> findNavController().popBackStack()
		}
	}
}

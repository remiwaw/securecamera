package com.rwawrzyniak.securephotos.ui.main.previewphotos.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import com.rwawrzyniak.securephotos.R
import com.rwawrzyniak.securephotos.core.android.BasicFragment
import com.rwawrzyniak.securephotos.ui.main.previewphotos.datasource.mapper.ImageModel
import com.rwawrzyniak.securephotos.ui.main.previewphotos.ui.PreviewPhotosViewModel.PreviewPhotosViewAction.OnLoadingFailed
import com.rwawrzyniak.securephotos.ui.main.previewphotos.ui.PreviewPhotosViewModel.PreviewPhotosViewAction.OnLoadingFinished
import com.rwawrzyniak.securephotos.ui.main.previewphotos.ui.PreviewPhotosViewModel.PreviewPhotosViewEffect
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.preview_photos_fragment.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class PreviewPhotosFragment constructor(
	private val imagesGridAdapter: ImagesGridAdapter,
	private val loadStateAdapter: ImagesLoadStateAdapter
) : BasicFragment(R.layout.preview_photos_fragment) {
	private val viewModel: PreviewPhotosViewModelImpl by viewModels()

	private val loadStateListener = fun(combinedLoadStates: CombinedLoadStates) {
		when (val state = combinedLoadStates.source.refresh) {
			is LoadState.NotLoading -> onLoadingFinished()
			is LoadState.Error -> onLoadingError(state.error)
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		setupUI()
		wireUpViewModel()
	}

	override fun onResume() {
		super.onResume()
		with(imagesGridAdapter) {
			addLoadStateListener(loadStateListener)
		}
	}

	override fun onPause() {
		with(imagesGridAdapter) {
			removeLoadStateListener(loadStateListener)
		}
		super.onPause()
	}

	private fun setupUI() {
		with(imagesRV) {
			layoutManager = GridLayoutManager(context, 2)
			adapter = imagesGridAdapter.withLoadStateFooter(loadStateAdapter)
		}
	}

	private fun wireUpViewModel() {
		lifecycleScope.launch {
			viewModel.observeState()
				.collectLatest { state -> handleStateChanges(state) }
		}

		lifecycleScope.launch {
			viewModel.observeEffect()
				.collectLatest { effect -> handleEffectChange(effect) }
		}
	}

	private suspend fun handleStateChanges(state: PreviewPhotosViewModel.PreviewPhotosViewState) {
		imagesCount.setText(state.loadedImagesCountText)

		if (state.noPhotosAvailable) {
			empty_view.visibility = View.VISIBLE
		} else {
			empty_view.visibility = View.GONE
		}

		state.pagingDataFlow?.let { showImages(it) }
	}

	private fun handleEffectChange(effect: PreviewPhotosViewEffect) {
		when (effect) {
			is PreviewPhotosViewEffect.ShowToastError -> {
				Toast.makeText(
					context,
					effect.errorMessage,
					Toast.LENGTH_LONG
				).show()
			}
		}
	}

	private fun onLoadingFinished() {
		viewModel.onAction(OnLoadingFinished(imagesGridAdapter.itemCount))
	}

	private fun onLoadingError(error: Throwable) {
		viewModel.onAction(
			OnLoadingFailed(error)
		)
	}

	private suspend fun showImages(pagingDataFlow: Flow<PagingData<ImageModel>>) {
		pagingDataFlow
			.collectLatest { pagingData ->
				imagesGridAdapter.submitData(
					viewLifecycleOwner.lifecycle,
					pagingData
				)
			}
	}
}

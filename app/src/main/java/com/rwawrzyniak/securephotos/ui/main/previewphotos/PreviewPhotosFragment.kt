package com.rwawrzyniak.securephotos.ui.main.previewphotos

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.rwawrzyniak.securephotos.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.preview_photos_fragment.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class PreviewPhotosFragment : Fragment(R.layout.preview_photos_fragment) {
	private val viewModel: PreviewPhotosViewModelImpl by viewModels()
	private val imagesGridAdapter = ImagesGridAdapter()
	private val loadStateListener = fun(combinedLoadStates: CombinedLoadStates) {
		when (combinedLoadStates.source.refresh) {
			is LoadState.NotLoading -> onLoadingFinished()
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
			layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
			adapter =  imagesGridAdapter.withLoadStateFooter(ImagesLoadStateAdapter())
		}
	}

	private fun wireUpViewModel() {
		viewModel.observeState()
			.onEach { state -> handleStateChanges(state) }
			.launchIn(lifecycleScope)
	}

	private suspend fun handleStateChanges(state: PreviewPhotosViewModel.PreviewPhotosViewState) {
		state.pagingDataFlow?.let { showImages(it) }
		state.isLoading
	}

	private fun onLoadingFinished() {
		viewModel.onAction(
			PreviewPhotosViewModel.PreviewPhotosViewAction.OnLoadingFinished(
				imagesGridAdapter.itemCount
			)
		)
	}

	private suspend fun showImages(pagingDataFlow: Flow<PagingData<ImageDto>>) {
		// TODO collect or collect latest?
		pagingDataFlow
			.collectLatest { pagingData ->
				imagesGridAdapter.submitData(
					viewLifecycleOwner.lifecycle,
					pagingData
				)
			}
	}
}

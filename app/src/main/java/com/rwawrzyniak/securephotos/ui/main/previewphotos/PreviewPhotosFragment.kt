package com.rwawrzyniak.securephotos.ui.main.previewphotos

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.rwawrzyniak.securephotos.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.preview_photos_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class PreviewPhotosFragment : Fragment(R.layout.preview_photos_fragment) {
	private val viewModel: PreviewPhotosViewModelImpl by viewModels()
	private val imagesGridAdapter: ImagesGridAdapter by lazy { ImagesGridAdapter() }
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
			adapter = imagesGridAdapter
		}
	}

	private fun wireUpViewModel() {
		CoroutineScope(Dispatchers.Main).launch {

			with(viewModel) {
				observeState()
					.collect(::handleStateChanges)

				observeEffect()
					.collect { handleEffect(it) }
			}
		}
	}

	private suspend fun handleStateChanges(state: PreviewPhotosViewState) {
		CoroutineScope(Dispatchers.Main).launch {
			when (state) {
				PreviewPhotosViewState.Initialising -> viewModel.onAction(PreviewPhotosViewAction.Initialize)
				is PreviewPhotosViewState.ShowImages -> showImages(state)
			}
		}
	}

	private fun handleEffect(effect: PreviewPhotosViewEffect) {
		when (effect) {
			PreviewPhotosViewEffect.ShowLoadingIndicator -> TODO()
			PreviewPhotosViewEffect.HideLoadingIndicator -> TODO()
		}
	}

	private fun onLoadingFinished() {
		CoroutineScope(Dispatchers.Main).launch {
			viewModel.onAction(PreviewPhotosViewAction.OnLoadingFinished(imagesGridAdapter.itemCount))
		}
	}

	private suspend fun showImages(state: PreviewPhotosViewState.ShowImages) {
		state.pagingDataFlow
			?.collect { pagingData -> imagesGridAdapter.submitData(viewLifecycleOwner.lifecycle, pagingData) }
	}
}

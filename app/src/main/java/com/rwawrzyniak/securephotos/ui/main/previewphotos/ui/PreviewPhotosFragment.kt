package com.rwawrzyniak.securephotos.ui.main.previewphotos.ui

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.rwawrzyniak.securephotos.R
import com.rwawrzyniak.securephotos.ui.main.permissions.PermissionFragment
import com.rwawrzyniak.securephotos.ui.main.permissions.PermissionFragment.Companion.createAndCommitPermissionFragment
import com.rwawrzyniak.securephotos.ui.main.previewphotos.datasource.mapper.ImageDto
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.preview_photos_fragment.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class PreviewPhotosFragment constructor(private val imagesGridAdapter: ImagesGridAdapter) : Fragment(R.layout.preview_photos_fragment) {
	private val viewModel: PreviewPhotosViewModelImpl by viewModels()
	private lateinit var andStoragePermissionFragment: PermissionFragment

	private val loadStateListener = fun(combinedLoadStates: CombinedLoadStates) {
		when (combinedLoadStates.source.refresh) {
			is LoadState.NotLoading -> onLoadingFinished()
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		andStoragePermissionFragment = createAndCommitPermissionFragment(
			CAMERA_PERMISSION_FRAGMENT_TAG,
			REQUIRED_PERMISSIONS
		)

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
			adapter =  imagesGridAdapter.withLoadStateHeaderAndFooter(
				header = ImagesLoadStateAdapter { imagesGridAdapter.retry() },
				footer = ImagesLoadStateAdapter { imagesGridAdapter.retry() }
			)
		}
	}

	private fun wireUpViewModel() {
		viewModel.observeState()
			.onEach { state -> handleStateChanges(state) }
			.launchIn(lifecycleScope)
	}

	private suspend fun handleStateChanges(state: PreviewPhotosViewModel.PreviewPhotosViewState) {
		if(!andStoragePermissionFragment.checkPermission()){
			showPermissionPermanentlyDeniedPopup(requireContext())
			findNavController().popBackStack()
		}
		state.pagingDataFlow?.let { showImages(it) }
	}

	private fun showPermissionPermanentlyDeniedPopup(context: Context) {
		Toast.makeText(
			context,
			context.getString(R.string.storage_permission_denied_permanently),
			Toast.LENGTH_LONG
		).show()
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

	companion object {
		private const val CAMERA_PERMISSION_FRAGMENT_TAG = "CAMERA_PERMISSION_FRAGMENT_TAG"
		private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
	}
}

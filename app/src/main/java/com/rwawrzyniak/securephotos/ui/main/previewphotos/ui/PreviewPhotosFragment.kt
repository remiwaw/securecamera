package com.rwawrzyniak.securephotos.ui.main.previewphotos.ui

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import com.rwawrzyniak.securephotos.R
import com.rwawrzyniak.securephotos.core.android.BasicFragment
import com.rwawrzyniak.securephotos.ui.main.permissions.PermissionFragment
import com.rwawrzyniak.securephotos.ui.main.permissions.PermissionFragment.Companion.createAndCommitPermissionFragment
import com.rwawrzyniak.securephotos.ui.main.previewphotos.datasource.mapper.ImageDto
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
	private lateinit var permissionFragment: PermissionFragment
	private var shouldSkipAppCode = false

	private val loadStateListener = fun(combinedLoadStates: CombinedLoadStates) {
		when (val state = combinedLoadStates.source.refresh) {
			is LoadState.NotLoading -> onLoadingFinished()
			is LoadState.Error -> onLoadingError(state.error)
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		permissionFragment = createAndCommitPermissionFragment(
			CAMERA_PERMISSION_FRAGMENT_TAG,
			REQUIRED_PERMISSIONS
		)
		shouldSkipAppCode = permissionFragment.shouldSkipAppCode()

		setupUI()
		wireUpViewModel()
	}

	override fun shouldSkipAppCode(): Boolean {
		return shouldSkipAppCode
	}

	override fun onResume() {
		super.onResume()
		shouldSkipAppCode = permissionFragment.shouldSkipAppCode()

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
			adapter =  imagesGridAdapter.withLoadStateFooter(loadStateAdapter)
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
		if(!permissionFragment.checkPermission()){
			showPermissionPermanentlyDeniedPopup(requireContext())
			findNavController().popBackStack()
		}

		imagesCount.setText(state.loadedImagesCountText)

		if(state.noPhotosAvailable){
			empty_view.visibility = View.VISIBLE
		} else {
			empty_view.visibility = View.GONE
		}

		state.pagingDataFlow?.let { showImages(it) }
	}

	private fun handleEffectChange(effect: PreviewPhotosViewEffect) {
		when(effect){
			is PreviewPhotosViewEffect.ShowToastError -> {
				Toast.makeText(
					context,
					effect.errorMessage,
					Toast.LENGTH_LONG
				).show()
			}
		}
	}

	private fun showPermissionPermanentlyDeniedPopup(context: Context) {
		Toast.makeText(
			context,
			context.getString(R.string.storage_permission_denied_permanently),
			Toast.LENGTH_LONG
		).show()
	}

	private fun onLoadingFinished() {
		viewModel.onAction(OnLoadingFinished(imagesGridAdapter.itemCount))
	}

	private fun onLoadingError(error: Throwable) {
		viewModel.onAction(
			OnLoadingFailed(error)
		)
	}

	private suspend fun showImages(pagingDataFlow: Flow<PagingData<ImageDto>>) {
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

package com.rwawrzyniak.securephotos.ui.main.previewphotos

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

internal sealed class PreviewPhotosViewState {
    internal object Initialising : PreviewPhotosViewState()

    internal data class ShowImages(
		val header: String,
        val pagingDataFlow: Flow<PagingData<ImageDto>>? = null
    ) : PreviewPhotosViewState()
}

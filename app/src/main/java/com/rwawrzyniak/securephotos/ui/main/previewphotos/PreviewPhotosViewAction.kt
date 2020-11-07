package com.rwawrzyniak.securephotos.ui.main.previewphotos

internal sealed class PreviewPhotosViewAction {
	data class OnLoadingFinished(val itemCount: Int) : PreviewPhotosViewAction()
	object Initialize : PreviewPhotosViewAction()
}

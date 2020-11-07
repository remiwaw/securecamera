package com.rwawrzyniak.securephotos.ui.main.previewphotos

internal sealed class PreviewPhotosViewEffect {
	object ShowLoadingIndicator : PreviewPhotosViewEffect()
	object HideLoadingIndicator : PreviewPhotosViewEffect()
}

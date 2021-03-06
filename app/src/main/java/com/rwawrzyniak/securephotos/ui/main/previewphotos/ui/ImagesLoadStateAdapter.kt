package com.rwawrzyniak.securephotos.ui.main.previewphotos.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rwawrzyniak.securephotos.R
import com.rwawrzyniak.securephotos.ui.main.previewphotos.ui.ImagesLoadStateAdapter.LoadingStateViewHolder
import javax.inject.Inject

internal class ImagesLoadStateAdapter @Inject constructor(private val retry: () -> Unit) : LoadStateAdapter<LoadingStateViewHolder>() {

	class LoadingStateViewHolder(itemView: View, retry: () -> Unit) :
		RecyclerView.ViewHolder(itemView) {

		// For some reason synthetic doesn't work
		private val textViewErrorMessage: TextView = itemView.findViewById(R.id.textViewErrorMessage)
		private val progressBar: ProgressBar = itemView.findViewById(R.id.progresBar)
		private val buttonRetry: Button = itemView.findViewById(R.id.buttonRetry)

		init {
			buttonRetry.setOnClickListener {
				retry.invoke()
			}
		}

		fun bindState(loadState: LoadState) {
			if (loadState is LoadState.Error) {
				textViewErrorMessage.text = loadState.error.localizedMessage
			}
			progressBar.isVisible = loadState is LoadState.Loading
			textViewErrorMessage.isVisible = loadState !is LoadState.Loading
			buttonRetry.isVisible = loadState !is LoadState.Loading
		}

	}

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadingStateViewHolder {
		val view = LayoutInflater
			.from(parent.context)
			.inflate(R.layout.loading_indicator_item, parent, false)

		return LoadingStateViewHolder(view, retry)
	}

    override fun onBindViewHolder(holder: LoadingStateViewHolder, loadState: LoadState) {
		holder.bindState(loadState)
    }
}

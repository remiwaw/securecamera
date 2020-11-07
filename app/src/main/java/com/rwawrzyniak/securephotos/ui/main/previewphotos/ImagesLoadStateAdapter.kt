package com.rwawrzyniak.securephotos.ui.main.previewphotos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rwawrzyniak.securephotos.R
import javax.inject.Inject

internal class ImagesLoadStateAdapter @Inject constructor(): LoadStateAdapter<ImagesLoadStateAdapter.ViewHolder>() {

    internal class ViewHolder(rootView: View) : RecyclerView.ViewHolder(rootView)

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ViewHolder = LayoutInflater
        .from(parent.context)
        .inflate(R.layout.loading_indicator_item, parent, false)
        .run {
            ViewHolder(this)
        }

    override fun onBindViewHolder(holder: ViewHolder, loadState: LoadState) {
        // do nothing
    }
}

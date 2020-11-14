package com.rwawrzyniak.securephotos.ui.main.previewphotos.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.rwawrzyniak.securephotos.R
import com.rwawrzyniak.securephotos.ui.main.previewphotos.datasource.mapper.ImageDto
import kotlinx.android.synthetic.main.image_item_in_grid_layout.view.*
import javax.inject.Inject

class ImagesGridAdapter @Inject constructor() : PagingDataAdapter<ImageDto, ImagesGridAdapter.ImageViewHolder>(DIFFER){

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder =
		ImageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.image_item_in_grid_layout, parent, false))

	override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
		val imageDto: ImageDto? = getItem(position)

		imageDto?.let {
			holder.iv.load(imageDto.bitmap){ placeholder(R.drawable.ic_baseline_lock_24) }
			holder.iv.setOnClickListener {
				// TODO here could we show image in full resolution.
			}
		}
	}

	class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
		val iv = view.iv as ImageView
	}

	companion object {
		private val DIFFER = DiffCallback()
	}
}

internal class DiffCallback : DiffUtil.ItemCallback<ImageDto>() {
	override fun areItemsTheSame(oldItem: ImageDto, newItem: ImageDto): Boolean =
		oldItem.title == newItem.title

	override fun areContentsTheSame(oldItem: ImageDto, newItem: ImageDto): Boolean =
		oldItem.title == newItem.title
}


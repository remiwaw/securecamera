package com.rwawrzyniak.securephotos.ui.main.previewphotos.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rwawrzyniak.securephotos.R
import com.rwawrzyniak.securephotos.ui.main.previewphotos.ImageDto
import kotlinx.android.synthetic.main.image_item_in_grid_layout.view.*
import javax.inject.Inject

// source: https://acomputerengineer.com/2019/05/09/display-image-grid-in-recyclerview-in-kotlin-android/
class ImagesGridAdapter @Inject constructor(): PagingDataAdapter<ImageDto, ImagesGridAdapter.ImageViewHolder>(DIFFER){

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder =
		ImageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.image_item_in_grid_layout, parent, false))

	override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
		val imageDto: ImageDto? = getItem(position)

		imageDto?.let {
			// TODO maybe resize it before? com.rwawrzyniak.securephotos.ui.main.previewphotos.datasource.mapper.ImageMapper.mapFromEntity
//			.resize(250, 250)
//			.centerCrop()

			holder.iv.setImageBitmap(imageDto.bitmap)
			holder.iv.setOnClickListener {
				//handle click event on image
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


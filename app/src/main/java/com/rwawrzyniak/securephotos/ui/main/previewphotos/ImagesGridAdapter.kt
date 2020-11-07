package com.rwawrzyniak.securephotos.ui.main.previewphotos

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.graphics.drawable.toDrawable
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rwawrzyniak.securephotos.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.image_item_in_grid_layout.view.*
import java.io.File

// source: https://acomputerengineer.com/2019/05/09/display-image-grid-in-recyclerview-in-kotlin-android/
class ImagesGridAdapter : PagingDataAdapter<ImageDto, ImagesGridAdapter.ColorViewHolder>(DiffCallback()){

	private val images: MutableList<Bitmap> = mutableListOf()

	override fun getItemCount(): Int = images.size

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder =
		ColorViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.image_item_in_grid_layout, parent, false))

	override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
		val bitmap: Bitmap = images[position]

		// TODO maybe resize it before? com.rwawrzyniak.securephotos.ui.main.previewphotos.datasource.mapper.ImageMapper.mapFromEntity
//			.resize(250, 250)
//			.centerCrop()

		holder.iv.setImageBitmap(bitmap)
		holder.iv.setOnClickListener {
			//handle click event on image
		}
	}

	class ColorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
		val iv = view.iv as ImageView
	}
}

internal class DiffCallback : DiffUtil.ItemCallback<ImageDto>() {
	override fun areItemsTheSame(oldItem: ImageDto, newItem: ImageDto): Boolean =
		oldItem.title == newItem.title

	override fun areContentsTheSame(oldItem: ImageDto, newItem: ImageDto): Boolean =
		oldItem.title == newItem.title
}
package com.rwawrzyniak.securephotos.ui.main.previewphotos.datasource.mapper

import com.rwawrzyniak.securephotos.core.android.EntityMapper
import com.rwawrzyniak.securephotos.data.model.ImageEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
class ImageMapper @Inject constructor(private val byteArrayBitMapMapper: ByteArrayBitMapMapper) :
	EntityMapper<ImageEntity, ImageDto> {

	override fun mapFromEntity(entity: ImageEntity): ImageDto {
		return ImageDto(entity.title, byteArrayBitMapMapper.mapFromEntity(entity.byteArray))
	}

	override fun mapToEntity(domainModel: ImageDto): ImageEntity {
		return ImageEntity(domainModel.title, byteArrayBitMapMapper.mapToEntity(domainModel.bitmap))
	}
}












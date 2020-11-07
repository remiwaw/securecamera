package com.rwawrzyniak.securephotos.ui.main.previewphotos.datasource.mapper

interface EntityMapper <Entity, DomainModel>{

    fun mapFromEntity(entity: Entity): DomainModel

    fun mapToEntity(domainModel: DomainModel): Entity
}

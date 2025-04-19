package br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.mapper.product

import br.com.rodrigogurgel.catalog.domain.entity.Product
import br.com.rodrigogurgel.catalog.domain.vo.Description
import br.com.rodrigogurgel.catalog.domain.vo.Id
import br.com.rodrigogurgel.catalog.domain.vo.Name
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.mapper.media.MediaRequestMapper.asEntity
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.mapper.product.ProductRequestMapper.asEntity
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.request.product.ProductRequestDTO
import java.util.UUID

object ProductRequestMapper {
    fun ProductRequestDTO.asEntity(id: UUID): Product = Product(
        id = Id(id),
        name = Name(name),
        description = description?.let { Description(it) },
        medias = medias.map { media -> media.asEntity() },
    )

    fun ProductRequestDTO.asEntity() = asEntity(UUID.randomUUID())
}

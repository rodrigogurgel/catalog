package br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.mapper.product

import br.com.rodrigogurgel.catalog.domain.entity.Product
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.mapper.media.MediaResponseMapper.asResponse
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.response.product.ProductResponseDTO

object ProductResponseMapper {
    fun Product.asResponse() = ProductResponseDTO(
        id = id.value.toString(),
        name = name.value,
        description = description?.value,
        medias = medias.map { media -> media.asResponse() }
    )
}

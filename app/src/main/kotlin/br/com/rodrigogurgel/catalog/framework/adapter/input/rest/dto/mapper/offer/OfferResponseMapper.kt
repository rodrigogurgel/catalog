package br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.mapper.offer

import br.com.rodrigogurgel.catalog.domain.entity.Offer
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.mapper.media.MediaResponseMapper.asResponse
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.mapper.offer.CustomizationResponseMapper.asResponse
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.mapper.product.ProductResponseMapper.asResponse
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.response.offer.OfferResponseDTO

object OfferResponseMapper {
    fun Offer.asResponse(): OfferResponseDTO = OfferResponseDTO(
        id = id.value.toString(),
        name = name.value,
        product = product?.asResponse(),
        price = price.value,
        status = status,
        customizations = customizations.map { customization ->
            customization.asResponse()
        },
        medias = medias.map { media ->
            media.asResponse()
        }
    )
}

package br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.mapper.offer

import br.com.rodrigogurgel.catalog.domain.entity.Option
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.mapper.media.MediaResponseMapper.asResponse
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.mapper.offer.CustomizationResponseMapper.asResponse
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.mapper.product.ProductResponseMapper.asResponse
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.response.option.OptionResponseDTO

object OptionResponseMapper {
    fun asResponse(option: Option): OptionResponseDTO = with(option) {
        return OptionResponseDTO(
            id = id.value.toString(),
            name = name.value,
            description = description?.value,
            product = product?.asResponse(),
            price = price.value,
            status = status,
            minPermitted = quantity.minPermitted,
            maxPermitted = quantity.maxPermitted,
            customizations = customizations.map { customization ->
                customization.asResponse()
            },
            medias = medias.map { media ->
                media.asResponse()
            }
        )
    }
}

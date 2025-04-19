package br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.mapper.offer

import br.com.rodrigogurgel.catalog.domain.entity.Customization
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.response.customization.CustomizationResponseDTO

object CustomizationResponseMapper {
    fun Customization.asResponse(): CustomizationResponseDTO = CustomizationResponseDTO(
        id = id.value.toString(),
        name = name.value,
        description = description?.value,
        status = status,
        minPermitted = quantity.minPermitted,
        maxPermitted = quantity.maxPermitted,
        options = options.map { option ->
            OptionResponseMapper.asResponse(option)
        }
    )
}

package br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.mapper.offer

import br.com.rodrigogurgel.catalog.domain.entity.Customization
import br.com.rodrigogurgel.catalog.domain.vo.Description
import br.com.rodrigogurgel.catalog.domain.vo.Id
import br.com.rodrigogurgel.catalog.domain.vo.Name
import br.com.rodrigogurgel.catalog.domain.vo.Quantity
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.mapper.offer.OptionRequestMapper.asEntity
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.request.offer.CustomizationRequestDTO
import java.util.UUID

object CustomizationRequestMapper {
    fun CustomizationRequestDTO.asEntity(): Customization = Customization(
        id = Id(id ?: UUID.randomUUID()),
        name = Name(name),
        description = description?.let { Description(it) },
        quantity = Quantity(minPermitted, maxPermitted),
        status = status,
        options = options.map { option ->
            option.asEntity()
        }.toMutableList()
    )
}

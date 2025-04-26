package br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.mapper.offer

import br.com.rodrigogurgel.catalog.domain.entity.Option
import br.com.rodrigogurgel.catalog.domain.entity.Product
import br.com.rodrigogurgel.catalog.domain.vo.Description
import br.com.rodrigogurgel.catalog.domain.vo.Id
import br.com.rodrigogurgel.catalog.domain.vo.Name
import br.com.rodrigogurgel.catalog.domain.vo.Price
import br.com.rodrigogurgel.catalog.domain.vo.Quantity
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.mapper.media.MediaRequestMapper.asEntity
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.mapper.offer.CustomizationRequestMapper.asEntity
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.request.offer.OptionRequestDTO
import java.util.UUID

object OptionRequestMapper {
    fun OptionRequestDTO.asEntity(): Option {
        val optionId = Id(id ?: UUID.randomUUID())
        return Option(
            id = optionId,
            name = Name(name),
            description = description?.let { Description(description) },
            product = product?.let {
                Product(
                    id = Id(product.id),
                    name = Name("OPTION${optionId.value}HOLDER"),
                    description = null,
                    medias = emptyList()
                )
            },
            price = Price(price),
            quantity = Quantity(minPermitted, maxPermitted),
            status = status,
            customizations = customizations?.map { customization ->
                customization.asEntity()
            }.orEmpty().toMutableList(),
            medias = medias.map { media -> media.asEntity() },
        )
    }
}

package br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.mapper.offer

import br.com.rodrigogurgel.catalog.domain.entity.Offer
import br.com.rodrigogurgel.catalog.domain.entity.Product
import br.com.rodrigogurgel.catalog.domain.vo.Description
import br.com.rodrigogurgel.catalog.domain.vo.Id
import br.com.rodrigogurgel.catalog.domain.vo.Name
import br.com.rodrigogurgel.catalog.domain.vo.Price
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.mapper.media.MediaRequestMapper.asEntity
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.mapper.offer.CustomizationRequestMapper.asEntity
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.request.offer.OfferRequestDTO
import java.util.UUID

object OfferRequestMapper {
    fun OfferRequestDTO.asEntity(offerId: UUID): Offer = Offer(
        id = Id(offerId),
        name = Name(name),
        description = description?.let { Description(description) },
        product = Product(
            id = Id(product.id),
            name = Name("OFFER${offerId}HOLDER"),
            description = null,
            medias = emptyList()
        ),
        price = Price(price),
        status = status,
        customizations = customizations?.map { customizationRequestDTO ->
            customizationRequestDTO.asEntity()
        }.orEmpty().toMutableList(),
        medias = medias.map { media -> media.asEntity() },
    )

    fun OfferRequestDTO.asEntity() = asEntity(UUID.randomUUID())
}

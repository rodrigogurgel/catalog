package br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.response.offer

import br.com.rodrigogurgel.catalog.domain.vo.Status
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.response.common.MediaDTO
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.response.customization.CustomizationResponseDTO
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.response.product.ProductResponseDTO
import java.math.BigDecimal

data class OfferResponseDTO(
    val id: String,
    val name: String,
    val product: ProductResponseDTO?,
    val price: BigDecimal,
    val status: Status,
    val customizations: List<CustomizationResponseDTO>,
    val medias: List<MediaDTO>,
)

package br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.response.option

import br.com.rodrigogurgel.catalog.domain.vo.Status
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.response.common.MediaDTO
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.response.customization.CustomizationResponseDTO
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.response.product.ProductResponseDTO
import java.math.BigDecimal

data class OptionResponseDTO(
    val id: String,
    val name: String,
    val product: ProductResponseDTO?,
    val minPermitted: Int,
    val maxPermitted: Int,
    val price: BigDecimal,
    val status: Status,
    val customizations: List<CustomizationResponseDTO>,
    val medias: List<MediaDTO>,
)

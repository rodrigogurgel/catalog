package br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.request.offer

import br.com.rodrigogurgel.catalog.domain.vo.Name
import br.com.rodrigogurgel.catalog.domain.vo.Status
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.request.common.GenericRequestIdDTO
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.response.common.MediaDTO
import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal

@Schema(description = "Payload for updating an offer.")
data class OfferRequestDTO(
    @field:Schema(
        description = "The name of the offer.",
        example = "Exclusive Summer Offer",
        type = "string",
        minLength = Name.MIN_LENGTH,
        maxLength = Name.MAX_LENGTH,
        required = true
    )
    val name: String,
    @field:Schema(
        description = "The unique identifier of the product associated with this offer.",
        required = false
    )
    val product: GenericRequestIdDTO,
    @field:Schema(
        description = "The price of the offer. It must be greater than zero.",
        example = "9.99",
        type = "double"
    )
    val price: BigDecimal,
    @field:Schema(
        description = "The status of the offer, which can be AVAILABLE or UNAVAILABLE.",
        type = "enum",
        required = true,
        implementation = Status::class
    )
    val status: Status,
    @field:Schema(
        description = "A list of customizations available for this offer.",
        required = false
    )
    val customizations: List<CustomizationRequestDTO>? = null,
    @field:Schema(
        description = "A list of customizations available for this option.",
        example = "[]",
        required = false
    )
    val medias: List<MediaDTO>,
)

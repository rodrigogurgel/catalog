package br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.request.offer

import br.com.rodrigogurgel.catalog.domain.vo.Name
import br.com.rodrigogurgel.catalog.domain.vo.Status
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.request.common.GenericRequestIdDTO
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.response.common.MediaDTO
import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.util.UUID

@Schema(description = "Payload for defining an option within a customization.")
data class OptionRequestDTO(
    @field:Schema(
        description = "The unique identifier of the option.",
        required = false
    )
    val id: UUID?,
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
        description = "The unique identifier of the product associated with this option.",
        required = false
    )
    val product: GenericRequestIdDTO?,
    @field:Schema(
        description = "The minimum number of times this option can be selected.",
        example = "0",
        type = "int",
        minimum = "0"
    )
    val minPermitted: Int,
    @field:Schema(
        description = "The maximum number of times this option can be selected.",
        example = "3",
        type = "int",
        minimum = "1"
    )
    val maxPermitted: Int,
    @field:Schema(
        description = "The price of this option.",
        example = "2.50",
        type = "double"
    )
    val price: BigDecimal,
    @field:Schema(
        description = "The status of the option, which can be AVAILABLE or UNAVAILABLE.",
        type = "enum",
        required = true,
        implementation = Status::class
    )
    val status: Status,
    @field:Schema(
        description = "A list of customizations available for this option.",
        example = "[]",
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

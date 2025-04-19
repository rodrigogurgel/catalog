package br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.request.product

import br.com.rodrigogurgel.catalog.domain.vo.Description
import br.com.rodrigogurgel.catalog.domain.vo.Name
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.response.common.MediaDTO
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Payload for updating a product.")
data class ProductRequestDTO(
    @field:Schema(
        description = "The updated name of the product.",
        example = "Updated Wireless Headphones",
        type = "string",
        minLength = Name.MIN_LENGTH,
        maxLength = Name.MAX_LENGTH,
        required = true
    )
    val name: String,
    @field:Schema(
        description = "The updated description of the product.",
        example = "Enhanced wireless headphones with improved battery life.",
        type = "string",
        minLength = Description.MIN_LENGTH,
        maxLength = Description.MAX_LENGTH,
        required = false
    )
    val description: String?,
    @field:Schema(
        description = "A list of customizations available for this option.",
        example = "[]",
        required = false
    )
    val medias: List<MediaDTO>,
)

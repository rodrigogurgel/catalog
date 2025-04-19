package br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.request.category

import br.com.rodrigogurgel.catalog.domain.vo.Description
import br.com.rodrigogurgel.catalog.domain.vo.Name
import br.com.rodrigogurgel.catalog.domain.vo.Status
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Payload for updating a category.")
data class CategoryRequestDTO(
    @field:Schema(
        description = "The updated name of the category.",
        example = "Updated Electronics",
        type = "string",
        minLength = Name.MIN_LENGTH,
        maxLength = Name.MAX_LENGTH,
        required = false
    )
    val name: String,
    @field:Schema(
        description = "The updated description of the category.",
        example = "Updated description for the electronics category.",
        type = "string",
        minLength = Description.MIN_LENGTH,
        maxLength = Description.MAX_LENGTH,
        required = false
    )
    val description: String?,
    @field:Schema(
        description = "The status of the category, which can be AVAILABLE or UNAVAILABLE.",
        type = "enum",
        required = true,
        implementation = Status::class
    )
    val status: Status,
)

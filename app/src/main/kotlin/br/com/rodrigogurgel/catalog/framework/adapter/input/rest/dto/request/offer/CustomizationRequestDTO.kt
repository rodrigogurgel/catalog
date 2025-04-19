package br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.request.offer

import br.com.rodrigogurgel.catalog.domain.vo.Description
import br.com.rodrigogurgel.catalog.domain.vo.Name
import br.com.rodrigogurgel.catalog.domain.vo.Status
import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

@Schema(description = "Payload for creating a customization.")
data class CustomizationRequestDTO(
    @field:Schema(
        description = "The unique identifier of the customization.",
        required = false
    )
    val id: UUID?,
    @field:Schema(
        description = "The name of the customization.",
        example = "Extra Cheese",
        type = "string",
        minLength = Name.MIN_LENGTH,
        maxLength = Name.MAX_LENGTH,
        required = true
    )
    val name: String,
    @field:Schema(
        description = "A detailed description of the customization.",
        example = "Adds extra cheese to your meal.",
        type = "string",
        minLength = Description.MIN_LENGTH,
        maxLength = Description.MAX_LENGTH,
        required = false
    )
    val description: String?,
    @field:Schema(
        description = "The minimum number of options that must be selected for this customization.",
        example = "0",
        type = "int",
        minimum = "0"
    )
    val minPermitted: Int,
    @field:Schema(
        description = "The maximum number of options that can be selected for this customization. " +
            "This value must be greater than or equal to the minimum permitted.",
        example = "3",
        type = "int",
        minimum = "1"
    )
    val maxPermitted: Int,
    @field:Schema(
        description = "The status of the customization, which can be AVAILABLE or UNAVAILABLE.",
        type = "enum",
        required = true,
        implementation = Status::class
    )
    val status: Status,
    @field:Schema(
        description = "A list of available options for this customization. " +
            "The list must contain at least one available option, and the number of available options " +
            "must be greater than or equal to the minimum permitted.",
        type = "array",
        required = true,
        minLength = 1
    )
    val options: List<OptionRequestDTO>,
)

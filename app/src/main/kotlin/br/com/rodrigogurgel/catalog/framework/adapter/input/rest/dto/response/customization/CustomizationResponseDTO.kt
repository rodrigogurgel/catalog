package br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.response.customization

import br.com.rodrigogurgel.catalog.domain.vo.Status
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.response.option.OptionResponseDTO

data class CustomizationResponseDTO(
    val id: String,
    val name: String,
    val description: String?,
    val minPermitted: Int,
    val maxPermitted: Int,
    val status: Status,
    val options: List<OptionResponseDTO>,
)

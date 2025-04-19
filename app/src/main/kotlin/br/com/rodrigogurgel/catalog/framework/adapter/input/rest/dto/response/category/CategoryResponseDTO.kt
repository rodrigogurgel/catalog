package br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.response.category

import br.com.rodrigogurgel.catalog.domain.vo.Status

data class CategoryResponseDTO(
    val id: String,
    val name: String,
    val description: String?,
    val status: Status,
)

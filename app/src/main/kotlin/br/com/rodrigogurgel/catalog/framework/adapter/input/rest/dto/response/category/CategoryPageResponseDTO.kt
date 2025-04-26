package br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.response.category

import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.response.common.PageResponseDTO

data class CategoryPageResponseDTO(
    override val limit: Int,
    override val nextCursor: String?,
    override val total: Int,
    override val data: List<CategoryResponseDTO>,
) : PageResponseDTO<CategoryResponseDTO>(limit, nextCursor, total, data)

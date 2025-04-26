package br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.response.product

import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.response.common.PageResponseDTO

data class ProductPageResponseDTO(
    override val limit: Int,
    override val nextCursor: String?,
    override val total: Int,
    override val data: List<ProductResponseDTO>,
) : PageResponseDTO<ProductResponseDTO>(limit, nextCursor, total, data)

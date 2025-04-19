package br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.response.offer

import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.response.common.PageResponseDTO

data class OfferPageResponseDTO(
    override val limit: Int,
    override val nextCursor: String?,
    override val total: Long,
    override val data: List<OfferResponseDTO>,
) : PageResponseDTO<OfferResponseDTO>(limit, nextCursor, total, data)

package br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.response.product

import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.response.common.MediaDTO

data class ProductResponseDTO(
    val id: String,
    val name: String,
    val description: String?,
    val medias: List<MediaDTO>,
)

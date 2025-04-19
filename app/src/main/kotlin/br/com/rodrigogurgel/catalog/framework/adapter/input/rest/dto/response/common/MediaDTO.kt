package br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.response.common

import br.com.rodrigogurgel.catalog.domain.vo.MediaType

data class MediaDTO(
    val url: String,
    val type: MediaType
)

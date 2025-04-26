package br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.response.common

abstract class PageResponseDTO<T>(
    open val limit: Int,
    open val nextCursor: String?,
    open val total: Int,
    open val data: List<T>,
)

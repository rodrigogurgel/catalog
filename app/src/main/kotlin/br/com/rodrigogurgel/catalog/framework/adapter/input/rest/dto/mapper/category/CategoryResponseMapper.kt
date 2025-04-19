package br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.mapper.category

import br.com.rodrigogurgel.catalog.domain.entity.Category
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.response.category.CategoryResponseDTO

object CategoryResponseMapper {
    fun Category.asResponse(): CategoryResponseDTO = CategoryResponseDTO(
        id = id.value.toString(),
        name = name.value,
        description = description?.value,
        status = status
    )
}

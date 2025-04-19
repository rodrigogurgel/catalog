package br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.mapper.category

import br.com.rodrigogurgel.catalog.domain.entity.Category
import br.com.rodrigogurgel.catalog.domain.vo.Description
import br.com.rodrigogurgel.catalog.domain.vo.Id
import br.com.rodrigogurgel.catalog.domain.vo.Name
import br.com.rodrigogurgel.catalog.framework.adapter.input.rest.dto.request.category.CategoryRequestDTO
import java.util.UUID

object CategoryRequestMapper {
    fun CategoryRequestDTO.asEntity(categoryId: UUID): Category = Category(
        id = Id(categoryId),
        name = Name(name),
        description = description?.let { Description(it) },
        status = status
    )

    fun CategoryRequestDTO.asEntity() = asEntity(UUID.randomUUID())
}

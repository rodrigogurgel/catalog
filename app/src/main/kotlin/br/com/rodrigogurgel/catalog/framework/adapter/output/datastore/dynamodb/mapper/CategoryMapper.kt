package br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.dynamodb.mapper

import br.com.rodrigogurgel.catalog.domain.entity.Category
import br.com.rodrigogurgel.catalog.domain.vo.Description
import br.com.rodrigogurgel.catalog.domain.vo.Id
import br.com.rodrigogurgel.catalog.domain.vo.Name
import br.com.rodrigogurgel.catalog.domain.vo.Status
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.dynamodb.model.CategoryModel
import java.time.Instant

object CategoryMapper {
    fun Category.asModel(storeId: Id): CategoryModel = CategoryModel(
        categoryId = id.value,
        storeId = storeId.value,
        name = name.value,
        description = description?.value,
        status = status.name,
        createdAt = Instant.now(),
        updatedAt = Instant.now(),
    )

    fun CategoryModel.asEntity(): Category = Category(
        id = Id(categoryId!!),
        name = Name(name!!),
        description = description?.let { Description(it) },
        status = Status.valueOf(status!!),
    )
}

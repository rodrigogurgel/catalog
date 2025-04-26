package br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.model

import org.springframework.data.mongodb.core.mapping.Field
import java.util.UUID

data class CategoryModelId(
    @Field("category_id")
    val categoryId: UUID,
    @Field("store_id")
    val storeId: UUID,
)

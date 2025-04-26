package br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.Instant

@Document(collection = "category")
data class CategoryModel(
    @Id
    val categoryModelId: CategoryModelId,
    @Field("name")
    val name: String,
    @Field("description")
    val description: String?,
    @Field("status")
    val status: String,
    @Field("created_at")
    val createdAt: Instant,
    @Field("updated_at")
    val updatedAt: Instant,
)

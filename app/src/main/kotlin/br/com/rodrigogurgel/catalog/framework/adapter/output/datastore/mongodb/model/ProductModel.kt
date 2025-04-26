package br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.Instant

@Document(collection = "product")
data class ProductModel(
    @Id
    val productModelId: ProductModelId,
    @Field("name")
    val name: String,
    @Field("description")
    val description: String?,
    @Field("medias")
    val medias: List<MediaModel>,
    @Field("created_at")
    val createdAt: Instant,
    @Field("updated_at")
    val updatedAt: Instant,
)

package br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Field
import java.time.Instant
import java.util.UUID

data class CustomizationModel(
    @Id
    val customizationId: UUID,
    @Field("name")
    val name: String,
    @Field("description")
    val description: String?,
    @Field("quantity")
    val quantity: QuantityModel,
    @Field("status")
    val status: String,
    @Field("options")
    val options: List<OptionModel>,
    @Field("created_at")
    val createdAt: Instant,
    @Field("updated_at")
    val updatedAt: Instant,
) {
    fun getProducts(): List<UUID> {
        return options.flatMap { optionModel -> optionModel.getProductIds() }
    }
}

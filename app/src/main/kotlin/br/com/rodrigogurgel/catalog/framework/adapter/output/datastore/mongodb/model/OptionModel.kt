package br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Field
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class OptionModel(
    @Id
    val optionId: UUID,
    @Field("product_id")
    val productId: UUID?,
    @Field("name")
    val name: String,
    @Field("description")
    val description: String?,
    @Field("quantity")
    val quantity: QuantityModel,
    @Field("status")
    val status: String,
    @Field("price")
    val price: BigDecimal,
    @Field("customizations")
    val customizations: List<CustomizationModel>,
    @Field("medias")
    val medias: List<MediaModel>,
    @Field("created_at")
    val createdAt: Instant,
    @Field("updated_at")
    val updatedAt: Instant,
) {
    fun getProductIds(): List<UUID> {
        val customizationProductIds = customizations
            .flatMap { customization -> customization.getProducts() }

        return productId?.let { customizationProductIds + it } ?: customizationProductIds
    }
}

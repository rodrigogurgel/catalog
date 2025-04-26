package br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.model

import org.springframework.data.mongodb.core.mapping.Field
import java.util.UUID

data class ProductOfferRelationModelId(
    @Field("store_id")
    val storeId: UUID,
    @Field("product_id")
    val productId: UUID,
    @Field("offer_id")
    val offerId: UUID,
)

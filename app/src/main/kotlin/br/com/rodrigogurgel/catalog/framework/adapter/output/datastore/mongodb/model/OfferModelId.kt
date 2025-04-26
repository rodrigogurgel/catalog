package br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.model

import org.springframework.data.mongodb.core.mapping.Field
import java.util.UUID

data class OfferModelId(
    @Field("offer_id")
    val offerId: UUID,
    @Field("category_id")
    val categoryId: UUID,
    @Field("store_id")
    val storeId: UUID
)

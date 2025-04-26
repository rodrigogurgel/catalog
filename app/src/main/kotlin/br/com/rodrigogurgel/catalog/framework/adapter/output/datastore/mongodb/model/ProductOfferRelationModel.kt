package br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.model

import org.springframework.data.annotation.Id

data class ProductOfferRelationModel(
    @Id
    val productOfferRelationModelId: ProductOfferRelationModelId
)

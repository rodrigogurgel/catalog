package br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.repository

import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.model.ProductOfferRelationModel
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.model.ProductOfferRelationModelId
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.UUID

@Suppress("FunctionNaming")
interface ProductOfferRelationRepository : MongoRepository<ProductOfferRelationModel, ProductOfferRelationModelId> {
    fun existsByProductOfferRelationModelId_ProductId(productId: UUID): Boolean
}

package br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.repository

import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.model.ProductOfferRelationModel
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.model.ProductOfferRelationModelId
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono
import java.util.UUID

@Suppress("FunctionNaming")
interface ProductOfferRelationRepository :
    ReactiveMongoRepository<ProductOfferRelationModel, ProductOfferRelationModelId> {
    fun existsByProductOfferRelationModelId_ProductId(productId: UUID): Mono<Boolean>
}

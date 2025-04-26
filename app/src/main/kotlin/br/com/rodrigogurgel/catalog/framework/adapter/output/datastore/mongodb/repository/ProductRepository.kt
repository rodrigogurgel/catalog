package br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.repository

import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.model.ProductModel
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.model.ProductModelId
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

@Suppress("FunctionNaming")
interface ProductRepository : ReactiveMongoRepository<ProductModel, ProductModelId> {
    fun countByProductModelId_StoreId(storeId: UUID): Mono<Int>
    fun existsByProductModelId_StoreIdAndProductModelId_ProductId(storeId: UUID, productId: UUID): Mono<Boolean>
    fun findByProductModelId_StoreIdAndProductModelId_ProductId(storeId: UUID, productId: UUID): Mono<ProductModel>
    fun findAllByProductModelId_StoreId(
        storeId: UUID,
        pageable: Pageable
    ): Flux<ProductModel>

    fun findAllByProductModelIdIn(productModelIds: List<ProductModelId>): Flux<ProductModel>

    fun deleteByProductModelId_StoreIdAndProductModelId_ProductId(storeId: UUID, productId: UUID): Mono<Unit>
}

package br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.repository

import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.model.ProductModel
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.model.ProductModelId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.UUID

@Suppress("FunctionNaming")
interface ProductRepository : MongoRepository<ProductModel, ProductModelId> {
    fun countByProductModelId_StoreId(storeId: UUID): Int
    fun existsByProductModelId_StoreIdAndProductModelId_ProductId(storeId: UUID, productId: UUID): Boolean
    fun findByProductModelId_StoreIdAndProductModelId_ProductId(storeId: UUID, productId: UUID): ProductModel?
    fun findAllByProductModelId_StoreId(
        storeId: UUID,
        pageable: Pageable
    ): Page<ProductModel>

    fun findAllByProductModelIdIn(productModelIds: List<ProductModelId>): List<ProductModel>

    fun deleteByProductModelId_StoreIdAndProductModelId_ProductId(storeId: UUID, productId: UUID)
}

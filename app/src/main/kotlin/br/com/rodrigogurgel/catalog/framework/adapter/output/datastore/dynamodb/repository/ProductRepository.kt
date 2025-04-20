package br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.dynamodb.repository

import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.dynamodb.model.ProductModel
import com.github.michaelbull.result.Result
import java.util.UUID

interface ProductRepository {
    suspend fun countProducts(storeId: UUID): Result<Long, Throwable>
    suspend fun create(productModel: ProductModel): Result<Unit, Throwable>
    suspend fun delete(storeId: UUID, productId: UUID): Result<Unit, Throwable>
    suspend fun exists(storeId: UUID, productId: UUID): Result<Boolean, Throwable>
    suspend fun findById(storeId: UUID, productId: UUID): Result<ProductModel?, Throwable>
    suspend fun getIfNotExists(storeId: UUID, productIds: List<UUID>): Result<List<UUID>, Throwable>
    suspend fun getProducts(
        storeId: UUID,
        limit: Int,
        cursor: String?
    ): Result<Pair<String?, List<ProductModel>>, Throwable>
    suspend fun productIsInUse(productId: UUID): Result<Boolean, Throwable>
    suspend fun update(productModel: ProductModel): Result<Unit, Throwable>
}

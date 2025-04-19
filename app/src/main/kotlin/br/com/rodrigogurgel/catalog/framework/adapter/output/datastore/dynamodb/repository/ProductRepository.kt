package br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.dynamodb.repository

import br.com.rodrigogurgel.catalog.domain.entity.Product
import br.com.rodrigogurgel.catalog.domain.vo.Id
import com.github.michaelbull.result.Result

interface ProductRepository {
    suspend fun countProducts(storeId: Id): com.github.michaelbull.result.Result<Long, Throwable>
    suspend fun create(storeId: Id, product: Product): com.github.michaelbull.result.Result<Unit, Throwable>
    suspend fun delete(storeId: Id, productId: Id): com.github.michaelbull.result.Result<Unit, Throwable>
    suspend fun exists(productId: Id): com.github.michaelbull.result.Result<Boolean, Throwable>
    suspend fun exists(storeId: Id, productId: Id): com.github.michaelbull.result.Result<Boolean, Throwable>
    suspend fun findById(storeId: Id, productId: Id): com.github.michaelbull.result.Result<Product?, Throwable>
    suspend fun getIfNotExists(productIds: List<Id>): com.github.michaelbull.result.Result<List<Id>, Throwable>
    suspend fun getProducts(
        storeId: Id,
        limit: Int,
        offset: Int
    ): com.github.michaelbull.result.Result<List<Product>, Throwable>
    suspend fun productIsInUse(productId: Id): com.github.michaelbull.result.Result<Boolean, Throwable>
    suspend fun update(storeId: Id, product: Product): Result<Unit, Throwable>
}

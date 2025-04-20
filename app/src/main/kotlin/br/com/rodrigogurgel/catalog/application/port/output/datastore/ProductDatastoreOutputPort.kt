package br.com.rodrigogurgel.catalog.application.port.output.datastore

import br.com.rodrigogurgel.catalog.domain.entity.Product
import br.com.rodrigogurgel.catalog.domain.vo.Id
import com.github.michaelbull.result.Result

interface ProductDatastoreOutputPort {
    suspend fun countProducts(storeId: Id): Result<Long, Throwable>
    suspend fun create(storeId: Id, product: Product): Result<Unit, Throwable>
    suspend fun delete(storeId: Id, productId: Id): Result<Unit, Throwable>
    suspend fun exists(storeId: Id, productId: Id): Result<Boolean, Throwable>
    suspend fun findById(storeId: Id, productId: Id): Result<Product?, Throwable>
    suspend fun getIfNotExists(storeId: Id, productIds: List<Id>): Result<List<Id>, Throwable>
    suspend fun getProducts(
        storeId: Id,
        limit: Int,
        cursor: String?
    ): Result<Pair<String?, List<Product>>, Throwable>
    suspend fun productIsInUse(productId: Id): Result<Boolean, Throwable>
    suspend fun update(storeId: Id, product: Product): Result<Unit, Throwable>
}

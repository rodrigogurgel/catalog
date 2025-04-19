package br.com.rodrigogurgel.catalog.framework.adapter.output.datastore

import br.com.rodrigogurgel.catalog.application.port.output.datastore.ProductDatastoreOutputPort
import br.com.rodrigogurgel.catalog.domain.entity.Product
import br.com.rodrigogurgel.catalog.domain.vo.Id
import com.github.michaelbull.result.Result
import org.springframework.stereotype.Component

@Component
class ProductDatastoreOutputPortAdapter : ProductDatastoreOutputPort {
    override suspend fun countProducts(storeId: Id): Result<Long, Throwable> {
        TODO("Not yet implemented")
    }

    override suspend fun create(
        storeId: Id,
        product: Product
    ): Result<Unit, Throwable> {
        TODO("Not yet implemented")
    }

    override suspend fun delete(
        storeId: Id,
        productId: Id
    ): Result<Unit, Throwable> {
        TODO("Not yet implemented")
    }

    override suspend fun exists(productId: Id): Result<Boolean, Throwable> {
        TODO("Not yet implemented")
    }

    override suspend fun exists(
        storeId: Id,
        productId: Id
    ): Result<Boolean, Throwable> {
        TODO("Not yet implemented")
    }

    override suspend fun findById(
        storeId: Id,
        productId: Id
    ): Result<Product?, Throwable> {
        TODO("Not yet implemented")
    }

    override suspend fun getIfNotExists(productIds: List<Id>): Result<List<Id>, Throwable> {
        TODO("Not yet implemented")
    }

    override suspend fun getProducts(
        storeId: Id,
        limit: Int,
        offset: Int
    ): Result<List<Product>, Throwable> {
        TODO("Not yet implemented")
    }

    override suspend fun productIsInUse(productId: Id): Result<Boolean, Throwable> {
        TODO("Not yet implemented")
    }

    override suspend fun update(
        storeId: Id,
        product: Product
    ): Result<Unit, Throwable> {
        TODO("Not yet implemented")
    }
}

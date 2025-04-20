package br.com.rodrigogurgel.catalog.framework.adapter.output.datastore

import br.com.rodrigogurgel.catalog.application.port.output.datastore.ProductDatastoreOutputPort
import br.com.rodrigogurgel.catalog.domain.entity.Product
import br.com.rodrigogurgel.catalog.domain.vo.Id
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.dynamodb.mapper.ProductMapper.asEntity
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.dynamodb.mapper.ProductMapper.asModel
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.dynamodb.repository.ProductRepository
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapCatching
import org.springframework.stereotype.Component

@Component
class ProductDatastoreOutputPortAdapter(
    private val productRepository: ProductRepository,
) : ProductDatastoreOutputPort {
    override suspend fun countProducts(storeId: Id): Result<Long, Throwable> =
        productRepository.countProducts(storeId.value)

    override suspend fun create(
        storeId: Id,
        product: Product
    ): Result<Unit, Throwable> = productRepository.create(product.asModel(storeId))

    override suspend fun delete(
        storeId: Id,
        productId: Id
    ): Result<Unit, Throwable> = productRepository.delete(storeId.value, productId.value)

    override suspend fun exists(
        storeId: Id,
        productId: Id
    ): Result<Boolean, Throwable> = productRepository.exists(storeId.value, productId.value)

    override suspend fun findById(
        storeId: Id,
        productId: Id
    ): Result<Product?, Throwable> =
        productRepository.findById(storeId.value, productId.value)
            .mapCatching { productModel -> productModel?.asEntity() }

    override suspend fun getIfNotExists(storeId: Id, productIds: List<Id>): Result<List<Id>, Throwable> =
        productRepository.getIfNotExists(storeId.value, productIds.map { productId -> productId.value })
            .mapCatching { productIds -> productIds.map { productId -> Id(productId) } }

    override suspend fun getProducts(
        storeId: Id,
        limit: Int,
        cursor: String?
    ): Result<Pair<String?, List<Product>>, Throwable> = productRepository.getProducts(storeId.value, limit, cursor)
        .mapCatching { nextCursorToProducts ->
            val nextCursor = nextCursorToProducts.first
            val categories = nextCursorToProducts.second.map { categoryModel -> categoryModel.asEntity() }

            nextCursor to categories
        }

    override suspend fun productIsInUse(productId: Id): Result<Boolean, Throwable> =
        productRepository.productIsInUse(productId.value)

    override suspend fun update(
        storeId: Id,
        product: Product
    ): Result<Unit, Throwable> = productRepository.update(product.asModel(storeId))
}

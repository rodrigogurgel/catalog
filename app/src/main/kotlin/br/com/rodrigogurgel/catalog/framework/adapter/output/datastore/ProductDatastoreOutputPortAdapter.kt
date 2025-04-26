package br.com.rodrigogurgel.catalog.framework.adapter.output.datastore

import br.com.rodrigogurgel.catalog.application.port.output.datastore.ProductDatastoreOutputPort
import br.com.rodrigogurgel.catalog.common.logger.extensions.CURSOR
import br.com.rodrigogurgel.catalog.common.logger.extensions.LIMIT
import br.com.rodrigogurgel.catalog.common.logger.extensions.PRODUCT
import br.com.rodrigogurgel.catalog.common.logger.extensions.PRODUCT_ID
import br.com.rodrigogurgel.catalog.common.logger.extensions.PRODUCT_IDS
import br.com.rodrigogurgel.catalog.common.logger.extensions.RESULT
import br.com.rodrigogurgel.catalog.common.logger.extensions.STORE_ID
import br.com.rodrigogurgel.catalog.common.logger.extensions.failure
import br.com.rodrigogurgel.catalog.common.logger.extensions.success
import br.com.rodrigogurgel.catalog.common.opentelemetry.extensions.coroutines.suspendSpan
import br.com.rodrigogurgel.catalog.domain.entity.Product
import br.com.rodrigogurgel.catalog.domain.vo.Id
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.exception.DatastoreIntegrationException
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.mapper.ProductMapper.asEntity
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.mapper.ProductMapper.asModel
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.model.OfferModel
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.model.ProductModelId
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.repository.ProductOfferRelationRepository
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.repository.ProductRepository
import br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.utils.CursorUtils
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapCatching
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import com.github.michaelbull.result.runCatching
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

@Component
class ProductDatastoreOutputPortAdapter(
    private val productRepository: ProductRepository,
    private val productOfferRelationRepository: ProductOfferRelationRepository
) : ProductDatastoreOutputPort {

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)

        private const val COUNT = "product-datastore-output-port-count-products"
        private const val CREATE = "product-datastore-output-port-create"
        private const val DELETE = "product-datastore-output-port-delete"
        private const val EXISTS = "product-datastore-output-port-exists"
        private const val FIND_BY_ID = "product-datastore-output-port-find-by-id"
        private const val GET_PRODUCTS = "product-datastore-output-port-get-products"
        private const val UPDATE = "product-datastore-output-port-update"
        private const val PRODUCT_IS_IN_USE = "product-datastore-output-port-product-is-in-use"
        private const val GET_IF_NOT_EXISTS = "product-datastore-output-port-get-if-not-exists"
    }

    override suspend fun countProducts(storeId: Id): Result<Int, Throwable> =
        suspendSpan(COUNT) {
            runCatching { productRepository.countByProductModelId_StoreId(storeId.value) }
                .mapError { DatastoreIntegrationException(it) }
                .onSuccess {
                    logger.success(
                        COUNT,
                        STORE_ID to storeId,
                        RESULT to it
                    )
                }
                .onFailure {
                    logger.failure(
                        COUNT,
                        it,
                        STORE_ID to storeId
                    )
                }
        }

    override suspend fun create(
        storeId: Id,
        product: Product
    ): Result<Unit, Throwable> = suspendSpan(CREATE) {
        runCatching<Unit> { productRepository.insert(product.asModel(storeId)) }
            .mapError { DatastoreIntegrationException(it) }
            .onSuccess {
                logger.success(
                    CREATE,
                    STORE_ID to storeId,
                    PRODUCT to product,
                    RESULT to it
                )
            }
            .onFailure {
                logger.failure(
                    CREATE,
                    it,
                    STORE_ID to storeId,
                    PRODUCT to product
                )
            }
    }

    override suspend fun delete(
        storeId: Id,
        productId: Id
    ): Result<Unit, Throwable> = suspendSpan(DELETE) {
        runCatching {
            productRepository.deleteByProductModelId_StoreIdAndProductModelId_ProductId(
                storeId.value,
                productId.value
            )
        }
            .mapError { DatastoreIntegrationException(it) }
            .onSuccess {
                logger.success(
                    DELETE,
                    STORE_ID to storeId,
                    PRODUCT_ID to productId,
                )
            }
            .onFailure {
                logger.failure(
                    DELETE,
                    it,
                    STORE_ID to storeId,
                    PRODUCT_ID to productId
                )
            }
    }

    override suspend fun exists(
        storeId: Id,
        productId: Id
    ): Result<Boolean, Throwable> = suspendSpan(EXISTS) {
        runCatching {
            productRepository.existsByProductModelId_StoreIdAndProductModelId_ProductId(
                storeId.value,
                productId.value
            )
        }
            .mapError { DatastoreIntegrationException(it) }
            .onSuccess {
                logger.success(
                    EXISTS,
                    STORE_ID to storeId,
                    PRODUCT_ID to productId,
                    RESULT to it
                )
            }
            .onFailure {
                logger.failure(
                    EXISTS,
                    it,
                    STORE_ID to storeId,
                    PRODUCT_ID to productId
                )
            }
    }

    override suspend fun findById(
        storeId: Id,
        productId: Id
    ): Result<Product?, Throwable> = suspendSpan(FIND_BY_ID) {
        runCatching {
            productRepository.findByProductModelId_StoreIdAndProductModelId_ProductId(
                storeId.value,
                productId.value
            )
        }
            .mapError { DatastoreIntegrationException(it) }
            .mapCatching { productModel -> productModel?.asEntity() }
            .onSuccess {
                logger.success(
                    FIND_BY_ID,
                    STORE_ID to storeId,
                    PRODUCT_ID to productId,
                    RESULT to it
                )
            }
            .onFailure {
                logger.failure(
                    FIND_BY_ID,
                    it,
                    STORE_ID to storeId,
                    PRODUCT_ID to productId
                )
            }
    }

    override suspend fun getIfNotExists(storeId: Id, productIds: List<Id>): Result<List<Id>, Throwable> =
        suspendSpan(GET_IF_NOT_EXISTS) {
            runCatching {
                val productModelIds = productIds.map { productId -> ProductModelId(productId.value, storeId.value) }
                val productIdsInDb =
                    productRepository.findAllByProductModelIdIn(productModelIds)
                        .map { it.productModelId.productId }
                productIds.filterNot { productId -> productIdsInDb.contains(productId.value) }
            }
                .mapError { DatastoreIntegrationException(it) }
                .onSuccess {
                    logger.success(
                        GET_IF_NOT_EXISTS,
                        STORE_ID to storeId,
                        PRODUCT_IDS to productIds,
                        RESULT to it
                    )
                }
                .onFailure {
                    logger.failure(
                        GET_IF_NOT_EXISTS,
                        it,
                        STORE_ID to storeId,
                        PRODUCT_IDS to productIds
                    )
                }
        }

    override suspend fun getProducts(
        storeId: Id,
        limit: Int,
        cursor: String?
    ): Result<List<Product>, Throwable> = suspendSpan(GET_PRODUCTS) {
        runCatching {
            val pageNumber = cursor?.let { CursorUtils.decode(cursor) } ?: 0
            val sort = Sort.by(Sort.Direction.ASC, OfferModel::createdAt.name)
            val pageRequest = PageRequest.of(pageNumber, limit, sort)

            val page = productRepository.findAllByProductModelId_StoreId(storeId.value, pageRequest)
            val products = page.toList().map { productModel -> productModel.asEntity() }

            products
        }
            .mapError { DatastoreIntegrationException(it) }
            .onSuccess {
                logger.success(
                    GET_PRODUCTS,
                    STORE_ID to storeId,
                    LIMIT to limit,
                    CURSOR to cursor,
                    RESULT to it
                )
            }
            .onFailure {
                logger.failure(
                    GET_PRODUCTS,
                    it,
                    STORE_ID to storeId,
                    LIMIT to limit,
                    CURSOR to cursor
                )
            }
    }

    override suspend fun productIsInUse(productId: Id): Result<Boolean, Throwable> =
        suspendSpan(PRODUCT_IS_IN_USE) {
            runCatching {
                productOfferRelationRepository.existsByProductOfferRelationModelId_ProductId(productId.value)
            }
                .mapError { DatastoreIntegrationException(it) }
                .onSuccess {
                    logger.success(
                        PRODUCT_IS_IN_USE,
                        PRODUCT_ID to productId,
                        RESULT to it
                    )
                }
                .onFailure {
                    logger.failure(
                        PRODUCT_IS_IN_USE,
                        it,
                        PRODUCT_ID to productId
                    )
                }
        }

    override suspend fun update(
        storeId: Id,
        product: Product
    ): Result<Unit, Throwable> = suspendSpan(UPDATE) {
        runCatching<Unit> { productRepository.save(product.asModel(storeId)) }
            .mapError { DatastoreIntegrationException(it) }
            .onSuccess {
                logger.success(
                    UPDATE,
                    STORE_ID to storeId,
                    PRODUCT to product,
                    RESULT to it
                )
            }
            .onFailure {
                logger.failure(
                    UPDATE,
                    it,
                    STORE_ID to storeId,
                    PRODUCT to product
                )
            }
    }
}
